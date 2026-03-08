package com.example.intentsday4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MyForegroundService : Service() {
    override fun onBind(intent: Intent?) = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, "note_channel")
            .setContentTitle("Note Upload")
            .setContentText("Uploading your note in the background...")
            .setSmallIcon(R.drawable.bell)
            .build()

        startForeground(1, notification)

        // Simulate work
        Handler(Looper.getMainLooper()).postDelayed({
//            stopForeground(true) // deprecated in java
            stopForeground(STOP_FOREGROUND_REMOVE)

            stopSelf()
        }, 5000)

        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "note_channel", "Note Uploads", NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
