package com.example.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class DatePickerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        calendar.time = arguments?.getSerializable(EXTRA_DATE) as Date

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val resultDate: Date = GregorianCalendar(
                    year,
                    month,
                    day,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
            ).time

            setFragmentResult(CrimeFragment.REQUEST_KEY_DATE, Bundle().apply {
                putSerializable(CrimeFragment.ARG_DATE, resultDate)
            })
        }

        return DatePickerDialog(
                requireContext(),
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    companion object {
        private const val EXTRA_DATE = "date"

        fun newInstance(date: Date): DialogFragment {
            return DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_DATE, date)
                }
            }
        }
    }
}