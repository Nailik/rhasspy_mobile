package org.rhasspy.mobile.nativeutils

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.MR

/**
 * creates necessary notification to hold services in background
 */
object ServiceNotification {

    private const val CHANNEL_ID = "org.rhasspy.mobile.services.channel.id"
    private const val GROUP_ID = "org.rhasspy.mobile.services.group.id"
    const val ONGOING_NOTIFICATION_ID = 324234


    fun create(): Notification {
        createGroup()
        createChannel()
        return NotificationCompat.Builder(Application.Instance, CHANNEL_ID)
            .setSmallIcon(MR.images.ic_launcher.drawableResId)
            .setContentTitle("Rhasspy Services Running")
            .setContentIntent(createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setAutoCancel(true)
            .build()
    }

    private fun createGroup() {
        NotificationManagerCompat
            .from(Application.Instance).createNotificationChannelGroup(
                NotificationChannelGroupCompat.Builder(GROUP_ID)
                    .setName("Rhasspy Service Notification Group")
                    .build()
            )
    }

    private fun createChannel() {
        NotificationManagerCompat
            .from(Application.Instance)
            .createNotificationChannel(
                NotificationChannelCompat.Builder(
                    CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_MIN
                )
                    .setName("Rhasspy Service Notification Channel")
                    .setDescription("Rhasspy Mobile runs in background for WakeWord detection and other services")
                    .setGroup(GROUP_ID)
                    .setShowBadge(false)
                    .build()
            )
    }

    private fun createPendingIntent(): PendingIntent {
        val launchIntent: Intent =
            Application.Instance.packageManager.getLaunchIntentForPackage(Application.Instance.packageName)!!

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(Application.Instance, 0, launchIntent, flags)
    }


}