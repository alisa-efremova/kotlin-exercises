package com.example.criminalintent

import com.github.terrakok.cicerone.androidx.FragmentScreen
import java.util.*

object Screens {
    fun CrimeList() = FragmentScreen { CrimeListFragment() }
    fun Crime(crimeId: UUID) = FragmentScreen { CrimeFragment.newInstance(crimeId) }
}