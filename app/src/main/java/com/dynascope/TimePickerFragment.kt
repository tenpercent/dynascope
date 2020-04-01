package com.dynascope

import android.os.Bundle
import androidx.preference.PreferenceDialogFragmentCompat

class TimePickerFragment() : PreferenceDialogFragmentCompat() {
    override fun onDialogClosed(positiveResult: Boolean) {
        positiveResult
    }
    companion object {
        fun newInstance(key: String) = TimePickerFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_KEY, key)
            }
        }
    }
}