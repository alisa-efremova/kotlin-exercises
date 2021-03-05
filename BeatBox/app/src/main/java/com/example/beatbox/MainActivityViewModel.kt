package com.example.beatbox

import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(private val beatBox: BeatBox) {

    val maxSpeed = 100
    val defaultSpeed = 0
    val speedLiveData = MutableLiveData<Int>().apply { postValue(defaultSpeed) }

    fun onSpeedRateChanged(speed: Int) {
        speedLiveData.postValue(speed)
        beatBox.speed = speed
    }
}