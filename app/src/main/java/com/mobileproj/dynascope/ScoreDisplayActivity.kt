package com.mobileproj.dynascope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.max


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

        private val newSessionButton: Button by lazy {
            findViewById<Button>(R.id.ns)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
//            findViewById<ViewPager2>(R.id.pager).currentItem = 1
            findViewById<ViewPager2>(R.id.pager).adapter = ContentAdapter2(this, sensorViewModel)
//            newSessionButton.apply {
//                setOnClickListener { sensorViewModel.resetSessionProgress(); it.visibility = INVISIBLE }
//                visibility = INVISIBLE
//            }
//            sensorViewModel.apply {
//                registerCounterObserver { c: Int ->
//                    findViewById<TextView>(R.id.totalCounter).text = resources.getQuantityString(R.plurals.score, c, c)
//                }
//                registerIntensityObserver { f: Float ->
//                    findViewById<ProgressBar>(R.id.intensity).progress = max(0F, f * 100).toInt()
//                }
//                registerSessionCounterObserver {
//                    findViewById<ProgressBar>(R.id.sessionProgress).apply {
//                        progress = (it / 30F).toInt()
//                        if (progress >= 100) {
//                            newSessionButton.visibility = VISIBLE
//                        }
//                    }
//                }
//            }
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
