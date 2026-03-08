package com.example.intentsday4

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class TaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Reminder: Check your note!", Toast.LENGTH_LONG).show()
    }
}
fun scheduleReminder(context: Context) {
    val intent = Intent(context, TaskReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerTime = System.currentTimeMillis() + 60000 // 1 minute

    // Use set() instead of setExact() to avoid restrictions
    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

