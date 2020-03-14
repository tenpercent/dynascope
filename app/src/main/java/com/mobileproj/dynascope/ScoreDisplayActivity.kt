package com.mobileproj.dynascope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider


class ScoreDisplayActivity(): AppCompatActivity() {

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
            sensorViewModel.registerObserver { c: Int -> findViewById<TextView>(R.id.counter).text = c.toString() }
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

