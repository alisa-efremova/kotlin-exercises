package com.example.criminalintent

import androidx.lifecycle.ViewModel
import com.example.criminalintent.db.CrimeRepository

class CrimeListViewModel : ViewModel() {
    private val repository = CrimeRepository.get()
    val crimeListLiveData = repository.getCrimes()

    fun addCrime(crime: Crime) {
        repository.insertCrime(crime)
    }
}