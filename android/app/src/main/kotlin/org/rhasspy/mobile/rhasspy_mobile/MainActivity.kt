package org.rhasspy.mobile.rhasspy_mobile

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        GeneratedPluginRegistrant.registerWith(flutterEngine)
        //    engine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint("lib/main.dart", "main"))
//https://github.com/flutter/flutter/issues/83292
        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "flutter_app_sync")

        channel.invokeMethod("started", null);

        channel.setMethodCallHandler { call, result ->
            if (call.method == "startwakeword") {

            } else {
                result.notImplemented()
            }
        }
        /*
        val intent = Intent(this, NativeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent)
        } else {
            applicationContext.startService(intent)
        }
        */

/*
        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)

        val intent = Intent(this, NativeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent)
        } else {
            applicationContext.startService(intent)
        }

        channel.setMethodCallHandler { call, result ->
            if (call.method == "getBatteryLevel") {

           //     porcupineManager.start();


            } else {
                result.notImplemented()
            }
        }*/
    }

}
