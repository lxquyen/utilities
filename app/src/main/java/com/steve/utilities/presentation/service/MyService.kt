package com.steve.utilities.presentation.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.steve.utilities.R
import com.steve.utilities.common.extensions.createNotificationChannel
import timber.log.Timber


class MyService : Service() {
    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val CHANNEL_DEFAULT = "CHANNEL_DEFAULT"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("onStartCommand")
        when (intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> startForegroundService()
            ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService()
            ACTION_PLAY -> Toast.makeText(applicationContext, "You click Play button.", Toast.LENGTH_LONG).show()
            ACTION_PAUSE -> Toast.makeText(applicationContext, "You click Pause button.", Toast.LENGTH_LONG).show()
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private fun startForegroundService() {
        createNotificationChannel(id = CHANNEL_DEFAULT, name = "test", importance = NotificationManager.IMPORTANCE_MAX)
        Timber.d("Start foreground service.")

        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_DEFAULT)
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_vcall)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_CALL)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
        startForeground(1, builder.build())
    }



    private fun stopForegroundService() {
        Timber.d("Stop foreground service.")
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.i("onBind")
        return null
    }

}