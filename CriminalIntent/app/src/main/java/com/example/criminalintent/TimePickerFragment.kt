package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.*

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: TimePickerFragmentArgs by navArgs()

        val calendar = Calendar.getInstance().apply {
            time = args.date
        }

        val timeSelectedListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val resultDate: Date = GregorianCalendar(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    hour,
                    minute
            ).time

            setFragmentResult(CrimeFragment.REQUEST_KEY_DATE, Bundle().apply {
                putSerializable(CrimeFragment.ARG_DATE, resultDate)
            })
        }

        return TimePickerDialog(
                requireContext(),
                timeSelectedListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
    }
}