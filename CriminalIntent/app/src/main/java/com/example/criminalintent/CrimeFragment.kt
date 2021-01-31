package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText


class CrimeFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private lateinit var crime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        initViews(view)
        return view
    }

    override fun onStart() {
        super.onStart()

        configureEditTextListener()
        configureButton()
        configureCheckbox()
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

    private fun configureButton() {
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }
    }

    private fun configureCheckbox() {
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }

    companion object {
        fun newInstance(): CrimeFragment {
            return CrimeFragment()
        }
    }

    private fun initViews(view: View) {
        titleEditText = view.findViewById(R.id.title_edit_text)
        dateButton = view.findViewById(R.id.date_button)
        solvedCheckBox = view.findViewById(R.id.solved_checkbox)
    }

}