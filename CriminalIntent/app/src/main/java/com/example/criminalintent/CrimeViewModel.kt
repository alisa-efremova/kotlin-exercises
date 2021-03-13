package com.example.criminalintent

import androidx.lifecycle.*
import com.example.criminalintent.db.CrimeRepository
import java.io.File
import java.util.*

private const val NAV_ARGS_KEY_CRIME_ID = "crimeId"

class CrimeViewModel(state: SavedStateHandle) : ViewModel() {
    private val repository = CrimeRepository.get()
    private val crimeIdLiveData = state.getLiveData<UUID?>(NAV_ARGS_KEY_CRIME_ID)

    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData) {
        crimeId -> repository.getCrime(crimeId)
    }

    fun saveCrime(crime: Crime) {
        repository.updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime): File {
        return repository.getPhotoFile(crime)
    }
}