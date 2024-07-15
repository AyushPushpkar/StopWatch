package com.example.stopwatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class NotificationManager : Application() {

     val CHANNEL_ID1 = "CHANNEL_ID1"
     val CHANNEL_ID2 = "CHANNEL_ID2"

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channel1 = NotificationChannel(CHANNEL_ID1, "High priority", NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "This my high priority Channel for Notification"

            val channel2 = NotificationChannel(CHANNEL_ID2, "Default priority", NotificationManager.IMPORTANCE_DEFAULT)
            channel2.description = "This my default priority Channel for Notification"

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager  //typecasting

            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)

        }
    }
}