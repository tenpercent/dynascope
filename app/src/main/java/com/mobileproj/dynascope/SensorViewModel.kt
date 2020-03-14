package com.mobileproj.dynascope

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*

// the responsibility is to hold displayed data independently of UI
class SensorViewModel(application: Application): AndroidViewModel(application) {

    private val sensorReadingsCapacity = 84L
    private val gyroReadingsCapacity = sensorReadingsCapacity * 3

    private var gyroReadingN = 0

    private val threshold = 5F
    var counter: LiveData<Int> = dao.count()

    val db: CounterDatabase get() = CounterDatabase.getDatabase(getApplication())

    val dao get() = db.counterDao()

    private var gyroReadings = ArrayDeque<Float>(gyroReadingsCapacity.toInt()).also {
        for (i in 0..gyroReadingsCapacity) {
            it.add(0F)
        }
    }

    fun receiveUpdate(values: Sequence<Float>) {
        synchronized(gyroReadings) {
            gyroReadings.addAll(values)
            while (gyroReadings.size > gyroReadingsCapacity) {
                gyroReadings.poll()
            }
        }
        gyroReadingN += 1
        val autocorr = gyroReadings.toFloatArray().autocorr(21)
        Log.d("debugsensorvm", "r: $gyroReadingN autocorr: ${autocorr}")
        if (autocorr > threshold) {
            viewModelScope.launch {
                dao.insert(CounterEntity(0))
            }
        }
    }
}