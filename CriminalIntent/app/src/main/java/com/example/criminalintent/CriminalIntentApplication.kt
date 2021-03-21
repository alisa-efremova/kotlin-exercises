package com.example.criminalintent

import android.app.Application
import com.example.criminalintent.db.CrimeRepository
import com.github.terrakok.cicerone.Cicerone

class CriminalIntentApplication : Application() {

    private val cicerone = Cicerone.create()
    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        CrimeRepository.initialize(this)
    }

    companion object {
        internal lateinit var INSTANCE: CriminalIntentApplication
            private set
    }
}