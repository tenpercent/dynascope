package com.dynascope

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.preference.PreferenceDialogFragmentCompat

class TimePickerFragment() : PreferenceDialogFragmentCompat() {
    /** What happens when OK or Cancel dialog button is pressed */
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }

            alarmMgr.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 10 * 1000,
                alarmIntent
            )
        }
    }
    companion object {
        fun newInstance(key: String) = TimePickerFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_KEY, key)
            }
        }
    }
}