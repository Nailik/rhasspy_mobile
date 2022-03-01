/*package org.rhasspy.mobile.rhasspy_mobile

import `in`.jvapps.system_alert_window.services.WindowServiceNew.CHANNEL_ID
import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.media.session.PlaybackState.ACTION_STOP
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class NativeService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val CHANNEL_ID_X = "PORCUPINE_CHANNEL_X"
        val ONGOING_NOTIFICATION_ID = 498

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Porcupine Channel"
            val descriptionText = "Porcupine Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID_X, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID_X)
            .setContentTitle("PORCUPINE_CHANNEL_X")
            .setContentIntent(pendingIntent)
            .build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(ONGOING_NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(ONGOING_NOTIFICATION_ID, notification)
        }

        val engine = FlutterEngine(applicationContext)
        GeneratedPluginRegistrant.registerWith(engine)
    //    engine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint("lib/main.dart", "main"))
//https://github.com/flutter/flutter/issues/83292
        val channel = MethodChannel(engine.dartExecutor.binaryMessenger, "flutter_app_sync")

        channel.invokeMethod("started", null);

        channel.setMethodCallHandler { call, result ->
            if (call.method == "startwakeword") {
                val porcupineManager = PorcupineManager.Builder()
                    .setAccessKey("${call.argument<String>("AccessKey")}")
                    .setKeywords(arrayOf(Porcupine.BuiltInKeyword.BUMBLEBEE))
                    .build(applicationContext) {
                        channel.invokeMethod("wakeword", null)
                    };
                porcupineManager.start();


            } else {
                result.notImplemented()
            }
        }

        return START_STICKY
    }



}*/