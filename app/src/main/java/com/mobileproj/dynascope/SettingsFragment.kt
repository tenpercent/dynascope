package com.mobileproj.dynascope

import android.os.Bundle
import android.util.Log
import androidx.preference.*

class SettingsFragment(private val viewModel: SensorViewModel) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        preferenceManager.apply {
            findPreference<SeekBarPreference>("duration")?.apply {
                onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                    viewModel.onDurationPreferenceChange(v)
                }
                value = viewModel.sessionDurationValue
            }
            findPreference<SeekBarPreference>("speed")?.apply {
                onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, v ->
                        viewModel.onSpeedPreferenceChange(v)
                    }
                value = viewModel.speedValue
            }
            findPreference<Preference>("reset")?.apply {
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    viewModel.resetSessionProgress()
                    true
                }
            }
            findPreference<SwitchPreferenceCompat>("voice")?.apply {
                onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener {_1, v ->
                        Log.d("debugsensor", "voice setting knob is $v")
                        true
                    }

            }
            findPreference<SwitchPreferenceCompat>("notifications")?.apply {
                onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener {_1, v ->
                        Log.d("debugsensor", "notification setting knob is $v")
                        true
                    }
            }
        }
    }
}
