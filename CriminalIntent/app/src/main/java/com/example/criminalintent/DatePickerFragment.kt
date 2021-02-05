package com.example.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val resultDate: Date = GregorianCalendar(year, month, day).time
            targetFragment?.let {
                (targetFragment as Callbacks).onDateSelected(resultDate)
            }
        }

        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance()
        calendar.time = date

        return DatePickerDialog(
                requireContext(),
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    companion object {
        fun newInstance(date: Date) : DialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}