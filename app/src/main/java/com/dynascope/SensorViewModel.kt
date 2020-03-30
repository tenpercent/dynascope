package com.dynascope

import android.app.Application
import android.content.Context
import android.os.Vibrator
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.ArrayDeque

/**
 * This class stores data that is displayed in UI but outlives it,
 * as fragments and activities may be destroyed at arbitrary moment.
 * We use the ViewModel library and subclass from [AndroidViewModel] in that library.
 *
 * Also, for simplicity, here we also perform talking to a database
 * and business logic related to motion intensity estimation from sensor readings
 */
class SensorViewModel(application: Application): AndroidViewModel(application) {

    // hardcoded constants:
    // the default duration of training session, in sensor readings
    // (the actual one may be changed in settings)
    private val defaultSessionDuration: Int = 3000
    // the default time lag for cosine similarity with self in the past, in sensor readings
    // (the actual time lag may be changed in settings)
    private val defaultTimeLag = 21
    // number of sensor readings to store
    private val sensorReadingsCapacity = 84
    // as cosine similarity is in range (-1, 1), and it's hard to get perfect similarity,
    // the rotations progress starts when the similarity is greater than this threshold
    private val threshold = .8F
    // each sensor reading is a triple, so this is the final storage capacity
    // for the data structure storing gyroscope readings
    private val gyroReadingsCapacity = sensorReadingsCapacity * 3

    // a window of sensor readings approximately 4 seconds long
    private val gyroReadings = ArrayDeque((0..gyroReadingsCapacity).map { it.toFloat() })
    private var gyroReadingN = 0

    private val db: CounterDatabase get() = CounterDatabase.getDatabase(getApplication())
    private val dao get() = db.counterDao()
    private val counter: LiveData<Int> = dao.count()
    private val intensity = MutableLiveData(0F)

    private val sessionCount = MutableLiveData(0)
    private val sessionCountValue get() = sessionCount.value ?: 0

    // setting knobs
    private val timeLag = MutableLiveData(defaultTimeLag)
    val speedValue: Int get() = timeLag.value ?: defaultTimeLag
    private val sessionDuration = MutableLiveData(defaultSessionDuration)
    val sessionDurationValue get() = sessionDuration.value ?: defaultSessionDuration
    val progress get(): Int = (sessionCountValue * 100F / sessionDurationValue).toInt()

    // cosine similarity with a time-lagged version of itself
    private val timeLagSimilarity get() =
        (speedValue-1..speedValue+1).map { gyroReadings.toFloatArray().autocorr(it)  }.max() ?: 0F

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
                sessionCount.postValue(sessionCountValue + 1)
            }
        }
        intensity.postValue(timeLagSimilarity)
    }

    fun resetSessionProgress() = sessionCount.postValue(0)

    fun registerCounterObserver(f: (Int) -> Unit) = counter.observeForever(f)
    fun registerIntensityObserver(f: (Float) -> Unit) = intensity.observeForever(f)
    fun registerSessionCounterObserver(f: (Int) -> Unit) = sessionCount.observeForever(f)

    fun onDurationPreferenceChange(newValue: Any?): Boolean {
        sessionDuration.postValue(newValue as Int)
        resetSessionProgress()
        return true
    }

    fun onSpeedPreferenceChange(newValue: Any?): Boolean {
        timeLag.postValue(newValue as Int)
        return true
    }
}
