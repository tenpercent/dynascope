package com.mobileproj.dynascope

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference

class SettingsFragment(private val viewModel: SensorViewModel) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        preferenceManager.apply {
            findPreference<SeekBarPreference>("duration")?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, v -> viewModel.onDurationPreferenceChange(v) }
            findPreference<SeekBarPreference>("speed")?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, v -> viewModel.onSpeedPreferenceChange(v) }
        }
    }
}