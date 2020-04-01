package com.dynascope

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.children

/**
 * UI for the page that displays settings
 * See also [R.xml.preferences]
 */
class SettingsFragment(private val viewModel: SensorViewModel) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        // Tie logic to UI elements by setting appropriate callbacks
        preferenceManager.preferenceScreen.children.forEach { p: Preference ->
            when (p.key) {
                "duration" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        viewModel.onDurationPreferenceChange(v)
                    }
                    // pull the stored value to UI
                    if (isAdded)
                        (p as SeekBarPreference).value = viewModel.sessionDurationValue
                }
                "speed" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        viewModel.onSpeedPreferenceChange(v)
                    }
                    // pull the stored value to UI
                    if (isAdded)
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
                        // TODO implement voice feedback
                        Log.d("debugsensor", "voice setting knob is $v")
                        true
                    }
                }
                "notifications" -> {
                    p.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
                        // TODO implement notifications
                        Log.d("debugsensor", "notification setting knob is $v")
                        true
                    }
                }
                "feedback" -> {
                    p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        val intent = Intent(ACTION_SENDTO).apply {
                            type = "*/*"
                            data = Uri.parse("mailto:")
                            // TODO replace fillers
                            putExtra(EXTRA_EMAIL, arrayOf("noreply@noreply.com"))
                            putExtra(EXTRA_SUBJECT, "Dynascope feedback")
                        }
                        startActivity(createChooser(intent, "Send email"))
                        true
                    }
                }
                "website" -> {
                    p.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        val intent = Intent(ACTION_VIEW).apply {
                            data = Uri.parse("https://github.com/tenpercent/dynascope")
                        }
                        startActivity(intent)
                        true
                    }
                }
                else -> {
                    Log.d("debugsensor", "no handler for $p")
                }
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is TimePickerPreference) {
            TimePickerFragment.newInstance(preference.key).also { tf ->
                tf.setTargetFragment(this, 1)
                fragmentManager?.also { mgr ->
                    tf.show(mgr, "DIALOG")
                }
            }
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}
