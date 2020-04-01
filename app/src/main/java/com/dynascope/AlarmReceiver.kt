package com.dynascope

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply {
            val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainFragmentActivity::class.java), FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this).apply {
                setDefaults(Notification.DEFAULT_ALL)
                setContentTitle("Time for exercise!")
                setSmallIcon(R.mipmap.ic_launcher)
                setContentIntent(pendingIntent)
            }.build()
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
        }
    }
}