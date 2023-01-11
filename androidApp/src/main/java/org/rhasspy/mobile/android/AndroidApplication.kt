package org.rhasspy.mobile.android

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.glance.appwidget.GlanceAppWidgetManager
import co.touchlab.kermit.Logger
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay
import org.rhasspy.mobile.android.widget.MicrophoneWidget
import org.rhasspy.mobile.nativeutils.NativeApplication
import kotlin.system.exitProcess

/**
 * holds android application and native functions and provides koin module
 */
class AndroidApplication : Application() {
    private val logger = Logger.withTag("AndroidApplication")

    init {
        Instance = this
    }

    companion object {
        lateinit var Instance: NativeApplication
            private set
    }

    init {
        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            logger.a(exception) {
                "uncaught exception in Thread $thread"
            }
            exitProcess(2)
        }
    }

    override fun onCreate() {
        super.onCreate()
        onCreated()
    }

    override fun startOverlay() {
        IndicationOverlay.start()
        MicrophoneOverlay.start()
    }

    override fun stopOverlay() {
        IndicationOverlay.stop()
        MicrophoneOverlay.stop()
    }

    override fun restart() {
        try {
            val packageManager: PackageManager = this.packageManager
            val intent: Intent = packageManager.getLaunchIntentForPackage(this.packageName)!!
            val componentName: ComponentName = intent.component!!
            val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
            this.startActivity(restartIntent)
            Runtime.getRuntime().exit(0)
        } catch (e: Exception) {
            Runtime.getRuntime().exit(0)
        }
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        if (!BuildConfig.DEBUG) {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
        }
    }

    override suspend fun updateWidget() {
        GlanceAppWidgetManager(this).getGlanceIds(MicrophoneWidget::class.java)
            .firstOrNull()
            ?.also {
                MicrophoneWidget().update(this, it)
            }
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

}