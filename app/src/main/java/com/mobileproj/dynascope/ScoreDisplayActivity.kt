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


class ScoreDisplayActivity(): FragmentActivity() {

        private val sensorManager: SensorManager by lazy {
            getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        private val gyroSensor: Sensor by lazy {
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        }
        private val sensorViewModel: SensorViewModel by lazy {
            ViewModelProvider(this).get(SensorViewModel::class.java)
        }

        private val gyroListener = object: SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                sensorViewModel.receiveUpdate(event?.values?.asSequence() as Sequence<Float>)
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            findViewById<ViewPager2>(R.id.pager).adapter = ContentAdapter(this, sensorViewModel)
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

class ContentAdapter constructor(activity: FragmentActivity, val viewmodel: SensorViewModel): FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ScoreScreenFragment(viewmodel)
            else -> SettingsFragment()
        }
    }
}