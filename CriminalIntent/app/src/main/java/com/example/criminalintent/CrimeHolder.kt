package com.example.criminalintent

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

open class CrimeHolder(view: View, callbacks: Callbacks?)
    : RecyclerView.ViewHolder(view) {

    private val titleTextView: TextView = view.findViewById(R.id.crime_title)
    private val dateTextView: TextView = view.findViewById(R.id.crime_date)
    private val solvedImageView: ImageView = view.findViewById(R.id.solved_crime)

    private lateinit var crime: Crime

    init {
        itemView.setOnClickListener {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    open fun bindCrime(crime: Crime) {
        this.crime = crime

        titleTextView.text = this.crime.title

        val pattern = "EEEE, MMM dd, yyyy"
        dateTextView.text = SimpleDateFormat(pattern, Locale.getDefault()).format(this.crime.date)

        solvedImageView.visibility = if (this.crime.isSolved) View.VISIBLE else View.GONE
    }
}

class StandardCrimeHolder(view: View, callbacks: Callbacks?)
    : CrimeHolder(view, callbacks)

class SeriousCrimeHolder(view: View, callbacks: Callbacks?)
    : CrimeHolder(view, callbacks) {
    private val callPoliceButton: Button = view.findViewById(R.id.call_police_button)

    init {
        callPoliceButton.setOnClickListener {
            val message = "Sorry, a call is temporary not available"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun bindCrime(crime: Crime) {
        super.bindCrime(crime)

        callPoliceButton.visibility = if (crime.isSolved) View.GONE else View.VISIBLE
    }
}