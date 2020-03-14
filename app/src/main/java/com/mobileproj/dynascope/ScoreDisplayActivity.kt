package com.mobileproj.dynascope

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import kotlin.math.max


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
            sensorViewModel.registerCounterObserver { c: Int ->
                findViewById<TextView>(R.id.counter).text = c.toString()
            }
            sensorViewModel.registerIntensityObserver { f: Float ->
                findViewById<TextView>(R.id.counter).background = ShapeDrawable(RectShape()).apply {
                    paint.color = Color.parseColor("#${f.toHex()}")
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

fun Float.toHex() = String.format("%02x%02x00", (0xFF * (1 - max(this, 0F)) * .5).toInt(), (0xFF * max(this, 0F)).toInt())