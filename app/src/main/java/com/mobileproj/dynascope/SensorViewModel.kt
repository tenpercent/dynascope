package com.mobileproj.dynascope

import android.app.Application
import android.content.Context
import android.os.Vibrator
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.util.*

// the responsibility is to hold displayed data independently of UI
class SensorViewModel(application: Application): AndroidViewModel(application) {

    private val sensorReadingsCapacity = 84
    private val gyroReadingsCapacity = sensorReadingsCapacity * 3

    private var gyroReadings = ArrayDeque((0..gyroReadingsCapacity).map { it.toFloat() })
    private var gyroReadingN = 0

    private val db: CounterDatabase get() = CounterDatabase.getDatabase(getApplication())
    private val dao get() = db.counterDao()
    private val counter: LiveData<Int> = dao.count()
    private var intensity = MutableLiveData(0F)

    private val sessionCount = MutableLiveData(0)

    // cosine similarity with a time-lagged version of itself
    private val timeLagSimilarity get() =
        (20..22).map { gyroReadings.toFloatArray().autocorr(it)  }.max() ?: 0F
    private val threshold = .8F

    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun receiveUpdate(values: Sequence<Float>) {
        gyroReadings.addAndTrim(values, gyroReadingsCapacity)
        gyroReadingN += 1
        Log.d("debugsensorvm", "r: $gyroReadingN autocorr: $timeLagSimilarity")
        if (timeLagSimilarity > threshold) {
            viewModelScope.launch {
                dao.insert(CounterEntity(0))
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
                sessionCount.postValue(sessionCount.value!! + 1)
            }
        }
        intensity.postValue(timeLagSimilarity)
    }

    fun resetSessionProgress() = sessionCount.postValue(0)

    fun registerCounterObserver(f: (Int) -> Unit) = counter.observeForever(f)
    fun registerIntensityObserver(f: (Float) -> Unit) = intensity.observeForever(f)
    fun registerSessionCounterObserver(f: (Int) -> Unit) = sessionCount.observeForever(f)
}

fun<T> ArrayDeque<T>.addAndTrim(values: Sequence<T>, capacity: Int) {
    synchronized(this) {
        addAll(values)
        while (size > capacity) {
            poll()
        }
    }
}