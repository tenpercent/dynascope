package com.mobileproj.dynascope;

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
//import kotlinx.coroutines.*
import java.util.*


class DynaScopeMotionLoggerActivity(): AppCompatActivity() {

        private lateinit var sensorManager: SensorManager

        private val gyroSensor: Sensor get() = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        private val accelSensor: Sensor get() = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        private val accelListener = object: SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                Log.d("debugsensor", "accel values: ${event?.values?.joinToString()}")
            }

        }

        private val gyroListener = object: SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                Log.d("debugsensor", "gyro values: ${event?.values?.joinToString()}")
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_main)

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }

        override fun onResume() {
            super.onResume()
            sensorManager.registerListener(gyroListener, gyroSensor, SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(accelListener, accelSensor, SENSOR_DELAY_NORMAL)
        }

        override fun onPause() {
            super.onPause()
            sensorManager.unregisterListener(gyroListener)
        }
    }