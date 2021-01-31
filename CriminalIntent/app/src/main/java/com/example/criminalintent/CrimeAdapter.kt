package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

private const val TYPE_STANDARD_CRIME = 0
private const val TYPE_SERIOUS_CRIME = 1

class CrimeAdapter(var crimes: List<Crime>)
    : RecyclerView.Adapter<CrimeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_SERIOUS_CRIME) {
            val view = layoutInflater.inflate(R.layout.list_item_serious_crime, parent, false)
            SeriousCrimeHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            StandardCrimeHolder(view)
        }
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.bindCrime(crime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (crimes[position].requiresPolice) TYPE_SERIOUS_CRIME else TYPE_STANDARD_CRIME
    }

    override fun getItemCount(): Int {
        return crimes.size
    }
}