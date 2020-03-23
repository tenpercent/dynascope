package com.mobileproj.dynascope

import android.os.Bundle
import android.util.Log
import androidx.preference.*

class SettingsFragment(private val viewModel: SensorViewModel) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        preferenceManager.preferenceScreen.children.forEach { p ->
            when (p.key) {
                "duration" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        viewModel.onDurationPreferenceChange(v)
                    }
                    (p as SeekBarPreference).value = viewModel.sessionDurationValue
                }
                "speed" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        viewModel.onSpeedPreferenceChange(v)
                    }
                    (p as SeekBarPreference).value = viewModel.speedValue
                }
                "reset" -> {
                    p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        viewModel.resetSessionProgress()
                        true
                    }
                }
                "voice" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        Log.d("debugsensor", "voice setting knob is $v")
                        true
                    }
                }
                "notifications" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {_, v ->
                        Log.d("debugsensor", "notification setting knob is $v")
                        true
                    }
                }
                else -> {
                    Log.d("debugsensor", "no handler for $p")
                }
            }
        }
    }
}
