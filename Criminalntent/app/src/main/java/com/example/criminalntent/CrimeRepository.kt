package com.example.criminalntent

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import database.CrimeDatabase
import database.migration_1_2
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"
private const val TAG = "CrimeRepository"

class CrimeRepository private constructor(context: Context) {

    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2)
        .build()
    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    fun getCrimes():  LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE =
                    CrimeRepository(context)
            }
        }
        fun get(): CrimeRepository {
    return INSTANCE ?:
    throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
            Log.i(TAG, "Updating $crime")
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
            Log.i(TAG, "Adding $crime")
        }
    }

    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)
}