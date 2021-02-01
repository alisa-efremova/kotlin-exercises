package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import java.util.*

private const val TYPE_STANDARD_CRIME = 0
private const val TYPE_SERIOUS_CRIME = 1

class CrimeAdapter(val callbacks: Callbacks?)
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
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).requiresPolice) TYPE_SERIOUS_CRIME else TYPE_STANDARD_CRIME
    }
}

interface Callbacks {
    fun onCrimeSelected(crimeId: UUID)
}