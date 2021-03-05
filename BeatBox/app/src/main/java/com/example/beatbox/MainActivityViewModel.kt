package com.example.beatbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    var beatBox: BeatBox? = null
    val maxSpeed = 100
    val defaultSpeed = 0
    val speedLiveData = MutableLiveData<Int>().apply { postValue(defaultSpeed) }

    fun onSpeedRateChanged(speed: Int) {
        speedLiveData.postValue(speed)
        beatBox?.speed = speed
    }

    override fun onCleared() {
        super.onCleared()
        beatBox?.release()
    }
}