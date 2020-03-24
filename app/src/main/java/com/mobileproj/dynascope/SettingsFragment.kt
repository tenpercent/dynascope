package com.mobileproj.dynascope

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.children

class SettingsFragment(private val viewModel: SensorViewModel) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        preferenceManager.preferenceScreen.children.forEach { p: Preference ->
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
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        Log.d("debugsensor", "notification setting knob is $v")
                        true
                    }
                }
                "feedback" -> {
                    p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        val intent = Intent(ACTION_SENDTO).apply {
                            type = "*/*"
                            data = Uri.parse("mailto:")
                            putExtra(EXTRA_EMAIL, arrayOf("noreply@noreply.com"))
                            putExtra(EXTRA_SUBJECT, "Dynascope feedback")
                        }
                        startActivity(createChooser(intent, "Send email"))
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
