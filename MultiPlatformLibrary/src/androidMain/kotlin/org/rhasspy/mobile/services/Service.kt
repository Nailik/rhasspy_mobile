package org.rhasspy.mobile.services

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.rhasspy.mobile.MR

actual abstract class Service : android.app.Service() {

    override fun onCreate() {
        super.onCreate()

        //define your channel id
        val CHANNEL_ID = "org.rhasspy.mobile.services.channel"
        val GROUP_ID = "org.rhasspy.mobile.services.group"
        val ONGOING_NOTIFICATION_ID = 324234

        val group = NotificationChannelGroupCompat.Builder(GROUP_ID)
            .setName("notfi")
            .build()

        NotificationManagerCompat
            .from(Application.Instance).createNotificationChannelGroup(group)

//create notification channel for android Oreo and above devices.
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName("rhasspy")
            .setDescription("rhasspy2")
            .setGroup(GROUP_ID)
            .build()

        NotificationManagerCompat
            .from(Application.Instance)
            .createNotificationChannel(channel)


        val launchIntent: Intent = Application.Instance.packageManager.getLaunchIntentForPackage(Application.Instance.packageName)!!

        val flags = if (SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(Application.Instance, 0, launchIntent, flags)

        val notification = NotificationCompat.Builder(Application.Instance, CHANNEL_ID)
            .setSmallIcon(MR.images.ic_launcher.drawableResId)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        stopServices()
        startServices()
        //TODO also stop

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        //TODO
        return null
    }

    actual abstract fun startServices()
    actual abstract fun stopServices()

}