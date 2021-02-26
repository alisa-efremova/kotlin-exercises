package com.example.criminalintent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var crimeImageView: ImageView
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var chooseSuspectButton: Button
    private lateinit var callSuspectButton: Button
    private lateinit var cameraButton: ImageButton
    private lateinit var pickContactLauncher: ActivityResultLauncher<Void>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val model: CrimeViewModel by lazy {
        ViewModelProvider(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()

        val args: CrimeFragmentArgs by navArgs()
        val crimeId: UUID = args.crimeId

        model.loadCrime(crimeId)

        registerFragmentListeners()
        registerLaunchers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        initViews(view)
        configureListeners()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.crimeLiveData.observe(
            viewLifecycleOwner,
            { crime ->
                crime?.let {
                    println("-- crime loaded")
                    this.crime = crime
                    photoFile = model.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(), "com.example.criminalintent.fileprovier", photoFile
                    )
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        configureEditTextListener()
        configureCheckboxListener()
    }

    override fun onStop() {
        super.onStop()

        model.saveCrime(crime)
    }

    private fun updateUI() {
        titleEditText.setText(crime.title)
        val datePattern = "EEEE, MMM dd, yyyy"
        val timePattern = "hh:mm aaa"
        dateButton.text = SimpleDateFormat(datePattern, Locale("en-US")).format(crime.date)
        timeButton.text = SimpleDateFormat(timePattern, Locale("en-US")).format(crime.date)

        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }

        if (crime.suspect.isNotEmpty()) {
            chooseSuspectButton.text = crime.suspect
        }

        callSuspectButton.isEnabled = crime.suspectId != 0

        updatePhotoView()
    }

    private fun registerFragmentListeners() {
        setFragmentResultListener(REQUEST_KEY_DATE) { key, result ->
            when (key) {
                REQUEST_KEY_DATE -> {
                    val date = result.getSerializable(ARG_DATE) as Date?
                    if (date != null) {
                        crime.date = date
                        updateUI()
                    }
                }
            }
        }
    }

    private fun registerLaunchers() {
        pickContactLauncher = registerForActivityResult(
                ActivityResultContracts.PickContact()
        ) { contactUri: Uri? ->
            parseContact(contactUri)
        }

        requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                tryToCallSuspect()
            }
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {}
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = PictureUtils().getScaledBitmap(photoFile.path, requireActivity())
            crimeImageView.setImageBitmap(bitmap)
        } else {
            crimeImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.placeholder, null))
        }
    }

    private fun configureListeners() {
        dateButton.setOnClickListener {
            findNavController().navigate(CrimeFragmentDirections.actionEditDate(crime.date))
        }

        timeButton.setOnClickListener {
            findNavController().navigate(CrimeFragmentDirections.actionEditTime(crime.date))
        }

        reportButton.setOnClickListener {
            sendReport()
        }

        chooseSuspectButton.apply {
            isEnabled = isContactIntentAvailable()

            setOnClickListener {
                pickContactLauncher.launch(null)
            }
        }

        cameraButton.apply {
            isEnabled = isCameraAvailable()

            setOnClickListener {
                takePictureLauncher.launch(photoUri)
            }
        }

        callSuspectButton.setOnClickListener {
            requestPermissionIfRequired()
        }
    }

    private fun parseContact(contactUri: Uri?) {
        if (contactUri == null) {
            return
        }

        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)
        val cursor = requireActivity().contentResolver.query(contactUri, queryFields,
                null, null, null)
        cursor?.use {
            if (it.count  == 0) {
                return
            }

            it.moveToFirst()
            crime.suspect = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            crime.suspectId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID)).toInt()

            model.saveCrime(crime)
            updateUI()
        }
    }

    private fun sendReport() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
        }.also { intent ->
            val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooserIntent)
        }
    }

    private fun isContactIntentAvailable(): Boolean {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        return isIntentAvailable(intent)
    }

    private fun isCameraAvailable(): Boolean {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        return isIntentAvailable(intent)
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(intent,
            PackageManager.MATCH_DEFAULT_ONLY)
        return resolvedActivity != null
    }

    private fun tryToCallSuspect() {
        val phoneNumber = getPhoneNumber(crime.suspectId)
        if (phoneNumber != null) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No phone numbers assigned", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPhoneNumber(contactId: Int): String? {
        val phoneCursor = requireActivity().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null)
        phoneCursor?.use { phones ->
            if (phones.moveToFirst()) {
                do {
                    val id = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID)).toInt()
                    if (id == contactId) {
                        return phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER))
                    }
                } while (phones.moveToNext())
            }
        }

        return null
    }

    private fun requestPermissionIfRequired() {
        if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            tryToCallSuspect()
        }
    }

    private fun configureEditTextListener() {
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
        titleEditText.addTextChangedListener(titleWatcher)
    }

    private fun configureCheckboxListener() {
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report,
                crime.title, dateString,
                solvedString, suspect)
    }

    private fun initViews(view: View) {
        titleEditText = view.findViewById(R.id.title_edit_text)
        crimeImageView = view.findViewById(R.id.crime_image_view)
        dateButton = view.findViewById(R.id.date_button)
        timeButton = view.findViewById(R.id.time_button)
        solvedCheckBox = view.findViewById(R.id.solved_checkbox)
        reportButton = view.findViewById(R.id.send_report_button)
        chooseSuspectButton = view.findViewById(R.id.choose_suspect_button)
        callSuspectButton = view.findViewById(R.id.call_suspect_button)
        cameraButton = view.findViewById(R.id.camera_button)
    }

    companion object {
        const val REQUEST_KEY_DATE = "requestKeyDate"
        const val ARG_DATE = "date"
    }
}