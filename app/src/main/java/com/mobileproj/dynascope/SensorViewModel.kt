package com.mobileproj.dynascope

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.util.*

// the responsibility is to hold displayed data independently of UI
class SensorViewModel(application: Application): AndroidViewModel(application) {

    private val sensorReadingsCapacity = 84
    private val gyroReadingsCapacity = sensorReadingsCapacity * 3

    private var gyroReadingN = 0

    private val threshold = .9F
    internal var counter: LiveData<Int> = dao.count()

    private val db: CounterDatabase get() = CounterDatabase.getDatabase(getApplication())

    private val dao get() = db.counterDao()

    private var gyroReadings = ArrayDeque((0..gyroReadingsCapacity).map { 0F })

    // cosine similarity with a time-lagged version of itself
    private val timeLagSimilarity get() = gyroReadings.toFloatArray().autocorr(21)

    fun receiveUpdate(values: Sequence<Float>) {
        gyroReadings.addAndTrim(values, gyroReadingsCapacity)
        gyroReadingN += 1
        Log.d("debugsensorvm", "r: $gyroReadingN autocorr: $timeLagSimilarity")
        if (timeLagSimilarity > threshold) {
            viewModelScope.launch {
                dao.insert(CounterEntity(0))
            }
        }
    }
}

fun<T> ArrayDeque<T>.addAndTrim(values: Sequence<T>, capacity: Int) {
    synchronized(this) {
        addAll(values)
        while (size > capacity) {
            poll()
        }
    }
}