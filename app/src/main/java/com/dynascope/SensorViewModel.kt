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
    /** the default duration of training session, in sensor readings
       (the actual one may be changed in settings) */
    private val defaultSessionDuration: Int = 3000
    /** the default time lag for cosine similarity with self in the past, in sensor readings
       (the actual time lag may be changed in settings) */
    private val defaultTimeLag = 21
    /** number of sensor readings to store */
    private val sensorReadingsCapacity = 84
    /** as cosine similarity is in range (-1, 1), and it's hard to get perfect similarity,
        the rotations progress starts when the similarity is greater than this threshold */
    private val threshold = .8F
    /** each sensor reading is a triple, so this is the final storage capacity
       for the data structure storing gyroscope readings */
    private val gyroReadingsCapacity = sensorReadingsCapacity * 3

    /** a window of sensor readings approximately 4 seconds long
        stored as deque, as past values are discarded once new values arrive */
    private val gyroReadings = ArrayDeque((0..gyroReadingsCapacity).map { it.toFloat() })
    /** current reading number */
    private var gyroReadingN = 0

    /** database related properties */
    private val db: CounterDatabase get() = CounterDatabase.getDatabase(getApplication())
    /** through this DAO, we can get the number of stored objects or insert a new object */
    private val dao get() = db.counterDao()
    /** number of sensor readings counted toward progress; the observer is set with [registerCounterObserver] */
    private val counter: LiveData<Int> = dao.count()
    /** current motion intensity; the observer is set with [registerIntensityObserver] */
    private val intensity = MutableLiveData(0F)
    /** number of sensor readings counted towards session progress; the observer is set with [registerSessionCounterObserver] */
    private val sessionCount = MutableLiveData(0)
    /** unwrap [MutableLiveData], setting a default value if wrapper is empty */
    private val sessionCountValue get() = sessionCount.value ?: 0

    /** time lag for computing similarity with self in the past */
    private val timeLag = MutableLiveData(defaultTimeLag)
    /** unwrap [MutableLiveData], setting a default value if wrapper is empty */
    val speedValue: Int get() = timeLag.value ?: defaultTimeLag
    /** training session duration, in sensor readings */
    private val sessionDuration = MutableLiveData(defaultSessionDuration)
    /** unwrap [MutableLiveData], setting a default value if wrapper is empty */
    val sessionDurationValue get() = sessionDuration.value ?: defaultSessionDuration
    /** training session progress percentage */
    val progress get(): Int = (sessionCountValue * 100F / sessionDurationValue).toInt()

    /** cosine similarity with a time-lagged version of itself */
    private val timeLagSimilarity get() =
        (speedValue-1..speedValue+1).map { gyroReadings.toFloatArray().autocorr(it)  }.max() ?: 0F

    /** a handle for invoking the device vibrations */
    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    /** this method is invoked via the sensor listener.
     * when new sensor readings arrive,
     * 1: the new readings are appended to the readings storage
     * 2: the past readings are discarded from the storage
     * 3: the similarity with self in the past is computed
     * 4: if the similarity passes a motion detection cutoff, a coroutine is launched that:
     *    4.1: inserts an object into the database (can't do this on UI thread, hence a coroutine)
     *    4.2: makes the device vibrate
     *    4.3: increments the training session counter
     * 5: the intensity is updated to reflect the computed similarity */
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

    /** starting a new training session resets the session counter */
    fun resetSessionProgress() = sessionCount.postValue(0)

    // these methods are called from UI and allow to refresh values in UI based on new values in this ViewModel
    /** [f] is called whenever [counter] changes
     *  with the new value of [counter] as the single argument to [f] */
    fun registerCounterObserver(f: (Int) -> Unit) = counter.observeForever(f)
    /** [f] is called whenever [intensity] changes
     *  with the new value of [intensity] as the single argument to [f] */
    fun registerIntensityObserver(f: (Float) -> Unit) = intensity.observeForever(f)
    /** [f] is called whenever [sessionCount] changes
     *  with the new value of [sessionCount] as the single argument to [f] */
    fun registerSessionCounterObserver(f: (Int) -> Unit) = sessionCount.observeForever(f)

    /** when the session duration setting is changed, the value provided in the setting is given to this method*/
    fun onDurationPreferenceChange(newValue: Any?): Boolean {
        sessionDuration.postValue(newValue as Int)
        resetSessionProgress()
        return true
    }

    /** when the rotation speed setting is changed, the value provided in the setting is given to this method*/
    fun onSpeedPreferenceChange(newValue: Any?): Boolean {
        timeLag.postValue(newValue as Int)
        return true
    }
}
