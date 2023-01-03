package org.rhasspy.mobile.android

import android.content.Intent
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
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(if (BuildConfig.DEBUG) false else enabled)
    }

    override suspend fun updateWidget() {
        GlanceAppWidgetManager(this).getGlanceIds(MicrophoneWidget::class.java)
            .firstOrNull()
            ?.also {
                MicrophoneWidget().update(this, it)

            }
    }

}