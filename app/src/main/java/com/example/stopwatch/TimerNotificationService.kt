package com.example.stopwatch

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.NotificationManager as SystemNotificationManager // Alias to avoid confusion


class TimerNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_NOT_STICKY
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, (application as MyNotificationManager).CHANNEL_ID1)

        // Customize your notification content
        notification.setContentTitle("Timer")
        notification.setContentText("Time's Up!")
        notification.setSmallIcon(R.drawable.timerred)
        notification.setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.RED)
            .setAutoCancel(true)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification.setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as SystemNotificationManager
        manager.notify(1, notification.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
