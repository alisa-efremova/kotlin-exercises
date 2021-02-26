package com.example.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import java.util.*

class MainActivity : AppCompatActivity(), Callbacks {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onCrimeSelected(crimeId: UUID) {
        val action = CrimeListFragmentDirections.actionEditCrime(crimeId)
        navController?.navigate(action)
    }
}