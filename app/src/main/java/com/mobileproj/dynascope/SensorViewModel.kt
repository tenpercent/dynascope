package com.mobileproj.dynascope

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import java.util.*

// the responsibility is to hold displayed data independently of UI
class SensorViewModel: ViewModel() {

    private val sensorReadingsCapacity = 84L
    private val gyroReadingsCapacity = sensorReadingsCapacity * 3

    private var gyroReadingN = 0

    private val threshold = 5F
    var counter: MutableLiveData<Int> = MutableLiveData(0)

//    val db: CounterDatabase by lazy {
//        Room.databaseBuilder()
//    }

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
            counter.postValue(1 + counter.value!!)
        }
    }
}