package com.mobileproj.dynascope

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference

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
        }
    }
}
