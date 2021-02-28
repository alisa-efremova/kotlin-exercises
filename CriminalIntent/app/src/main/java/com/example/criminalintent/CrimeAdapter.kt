package com.example.criminalintent

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import java.util.*

private const val TYPE_STANDARD_CRIME = 0
private const val TYPE_SERIOUS_CRIME = 1
private const val DESCR_DATE_FORMAT = "EEEE, dd MMMM"

class CrimeAdapter(val context: Context, val callbacks: Callbacks?)
    : ListAdapter<Crime, CrimeHolder>(CrimeItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_SERIOUS_CRIME) {
            val view = layoutInflater.inflate(R.layout.list_item_serious_crime, parent, false)
            SeriousCrimeHolder(view, callbacks)
        } else {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            StandardCrimeHolder(view, callbacks)
        }
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = getItem(position)
        holder.bindCrime(crime)


        holder.itemView.contentDescription = getContentDescription(crime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).requiresPolice) TYPE_SERIOUS_CRIME else TYPE_STANDARD_CRIME
    }

    private fun getContentDescription(crime: Crime): String {
        val dateString = DateFormat.format(DESCR_DATE_FORMAT, crime.date).toString()

        val title = if (crime.title.isNotEmpty())
            crime.title
        else
            context.getString(R.string.no_title_description)

        val crimeType = if (crime.requiresPolice)
            context.getString(R.string.strict_crime_description)
        else
            context.getString(R.string.standard_crime_description)

        val solved = if (crime.isSolved)
            context.getString(R.string.crime_report_solved)
        else
            context.getString(R.string.crime_report_unsolved)

        return context.getString(R.string.crime_list_item_description, crimeType, title, dateString, solved)
    }
}

interface Callbacks {
    fun onCrimeSelected(crimeId: UUID)
}