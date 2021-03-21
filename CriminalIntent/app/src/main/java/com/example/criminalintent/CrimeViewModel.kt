package com.example.criminalintent

import androidx.lifecycle.*
import com.example.criminalintent.db.CrimeRepository
import java.io.File
import java.util.*

class CrimeViewModel(state: SavedStateHandle) : ViewModel() {
    private val repository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()


    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData) {
        crimeId -> repository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID) {
        if (crimeIdLiveData.value != crimeId) {
            crimeIdLiveData.value = crimeId
        }
    }

    fun saveCrime(crime: Crime) {
        repository.updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime): File {
        return repository.getPhotoFile(crime)
    }
}