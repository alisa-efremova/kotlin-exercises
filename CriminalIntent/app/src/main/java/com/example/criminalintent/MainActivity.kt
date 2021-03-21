package com.example.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.androidx.AppNavigator
import java.util.*

class MainActivity : AppCompatActivity(), Callbacks {

    private val navigator = AppNavigator(this, R.id.main_container)
    private val router = CriminalIntentApplication.INSTANCE.router

    override fun onResumeFragments() {
        super.onResumeFragments()
        CriminalIntentApplication.INSTANCE.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        CriminalIntentApplication.INSTANCE.navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router.navigateTo(Screens.CrimeList())
    }

    override fun onCrimeSelected(crimeId: UUID) {
        router.navigateTo(Screens.Crime(crimeId))
    }
}