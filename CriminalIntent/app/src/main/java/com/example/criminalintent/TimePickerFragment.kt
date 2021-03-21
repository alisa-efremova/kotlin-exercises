package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance().apply {
            time = arguments?.getSerializable(EXTRA_DATE) as Date
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

    companion object {
        private const val EXTRA_DATE = "date"

        fun newInstance(date: Date): DialogFragment {
            return TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_DATE, date)
                }
            }
        }
    }
}