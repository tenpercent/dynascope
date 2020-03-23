package com.mobileproj.dynascope

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference

class SettingsFragment(private val viewModel: SensorViewModel) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        preferenceManager.findPreference<SeekBarPreference>("duration")?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue -> viewModel.onDurationPreferenceChange(newValue) }
    }
}