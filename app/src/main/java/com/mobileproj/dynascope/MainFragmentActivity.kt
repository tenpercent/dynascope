package com.mobileproj.dynascope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * The main and the only activity in this application.
 * It subclasses from [FragmentActivity] as we are using [ViewPager2]
 * and the [FragmentStateAdapter] that provides [Fragment]s to the [ViewPager2] to display
 * needs a reference to [FragmentActivity] in its constructor.
 */
class MainFragmentActivity(): FragmentActivity() {

    /**
     * Retrieves [Sensor]s, attaches and detaches [SensorEventListener]s
     */
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    /**
     * Gyroscope [Sensor] produces a stream of (x, y, z) triples
     * Values are larger with faster acceleration
     */
    private val gyroSensor: Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    /**
     * Holds the data that outlives UI
     */
    private val sensorViewModel: SensorViewModel by lazy {
        ViewModelProvider(this).get(SensorViewModel::class.java)
    }

    /**
     * Sends sensor values to [sensorViewModel] on each sensor update.
     * [sensorViewModel] does some computations that UI will pull to display
     */
    private val gyroListener = object: SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            sensorViewModel.receiveUpdate(event?.values?.asSequence() as Sequence<Float>)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * Tie [Fragment]s logic to [ViewPager2] UI container
         * with [FragmentStateAdapter] converting [Int] page number into a [Fragment]
         */
        findViewById<ViewPager2>(R.id.pager).adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> ScoreScreenFragment(sensorViewModel)
                else -> SettingsFragment(sensorViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(gyroListener, gyroSensor, 50000)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(gyroListener)
    }
}