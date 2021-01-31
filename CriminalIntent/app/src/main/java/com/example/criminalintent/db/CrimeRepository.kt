package com.example.criminalintent.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.Crime
import java.lang.IllegalStateException
import java.util.*

private const val DATABASE_NAME = "crime-database"

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN requiresPolice INTEGER NOT NULL DEFAULT 0")
    }
}

class CrimeRepository private constructor(context: Context) {

    private val database: CrimeDatabase = Room.databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME)
        .addMigrations(MIGRATION_1_2)
        .build()

    private val crimeDao = database.crimeDao()

    fun getCrimes(): LiveData<List<Crime>> {
        return crimeDao.getCrimes()
    }

    fun getCrime(id: UUID): LiveData<Crime?> {
        return crimeDao.getCrime(id)
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("Crime repository must be initialized")
        }
    }
}