package com.example.criminalintent

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import java.text.SimpleDateFormat
import java.util.*

private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_DATE = 0
private const val REQUEST_TIME = 1
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var titleEditText: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var chooseSuspectButton: Button
    private lateinit var pickContactLauncher: ActivityResultLauncher<Void>

    private lateinit var crime: Crime

    private val model: CrimeViewModel by lazy {
        ViewModelProvider(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()

        val args: CrimeFragmentArgs by navArgs()
        val crimeId: UUID = args.crimeId

        model.loadCrime(crimeId)

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

    private fun registerLaunchers() {
        pickContactLauncher = registerForActivityResult(
                ActivityResultContracts.PickContact()
        ) { contactUri: Uri? ->
            parseContact(contactUri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.crimeLiveData.observe(
            viewLifecycleOwner,
            { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        configureEditTextListener()
        configureCheckboxListener()
        updateUI()
    }

    override fun onStop() {
        super.onStop()

        model.saveCrime(crime)
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
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
    }

    private fun configureListeners() {
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
            }
        }

        reportButton.setOnClickListener {
            sendReport()
        }

        chooseSuspectButton.apply {
            setOnClickListener {
                chooseSuspect()
            }
            isEnabled = isContactIntentAvailable()
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
        val packageManager: PackageManager = requireActivity().packageManager
        val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY)
        return resolvedActivity != null
    }

    private fun chooseSuspect() {
        pickContactLauncher.launch(null)
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
        dateButton = view.findViewById(R.id.date_button)
        timeButton = view.findViewById(R.id.time_button)
        solvedCheckBox = view.findViewById(R.id.solved_checkbox)
        reportButton = view.findViewById(R.id.send_report_button)
        chooseSuspectButton = view.findViewById(R.id.choose_suspect_button)
    }

}