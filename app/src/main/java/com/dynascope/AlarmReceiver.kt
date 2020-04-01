package com.dynascope

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.apply {
            val pendingIntent = PendingIntent.getActivity(this,
                1001,
                Intent(applicationContext, MainFragmentActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }, FLAG_ONE_SHOT)
            val builder = NotificationCompat.Builder(this, "chan").apply {
                setAutoCancel(true)
                setDefaults(Notification.DEFAULT_ALL)
                setContentTitle("Time for exercise!")
                setContentText("")
                setContentInfo("")
                setSmallIcon(R.mipmap.ic_launcher)
                setContentIntent(pendingIntent)
                setWhen(System.currentTimeMillis() + 1000)
            }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(NotificationChannel("chan", "chan", IMPORTANCE_HIGH))
            }
            nm.notify(10001, builder.build())
        }
    }
}