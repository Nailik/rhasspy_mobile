package org.rhasspy.mobile.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.rhasspy.mobile.MR

object ServiceNotification {

    private const val CHANNEL_ID = "org.rhasspy.mobile.services.channel"
    private const val GROUP_ID = "org.rhasspy.mobile.services.group"
    const val ONGOING_NOTIFICATION_ID = 324234

    fun create(): Notification {
        createGroup()
        createChannel()
        return NotificationCompat.Builder(Application.Instance, CHANNEL_ID)
            .setSmallIcon(MR.images.ic_launcher.drawableResId)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setContentIntent(createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
    }

    fun createGroup() {
        NotificationManagerCompat
            .from(Application.Instance).createNotificationChannelGroup(
                NotificationChannelGroupCompat.Builder(GROUP_ID)
                    .setName("notfi")
                    .build()
            )
    }

    fun createChannel() {
        NotificationManagerCompat
            .from(Application.Instance)
            .createNotificationChannel(
                NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setName("rhasspy")
                    .setDescription("rhasspy2")
                    .setGroup(GROUP_ID)
                    .build()
            )
    }

    fun createPendingIntent(): PendingIntent {
        val launchIntent: Intent = Application.Instance.packageManager.getLaunchIntentForPackage(Application.Instance.packageName)!!

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(Application.Instance, 0, launchIntent, flags)
    }


}