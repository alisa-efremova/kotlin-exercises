package com.example.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val timeSelectedListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val resultDate: Date = GregorianCalendar(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    hour,
                    minute
            ).time
            targetFragment?.let {
                (targetFragment as DatePickerFragment.Callbacks).onDateSelected(resultDate)
            }
        }

        return TimePickerDialog(
                requireContext(),
                timeSelectedListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
    }

    companion object {
        fun newInstance(date: Date) : TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}