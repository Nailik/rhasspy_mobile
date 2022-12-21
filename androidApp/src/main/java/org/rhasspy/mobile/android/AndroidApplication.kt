package org.rhasspy.mobile.android

import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import co.touchlab.kermit.Logger
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.NativeApplication
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay
import org.rhasspy.mobile.android.widget.MicrophoneWidget
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
            logger.e(exception) {
                "uncaught exception in Thread $thread"
            }
            exitProcess(2)
        }
    }

    override fun onCreate() {
        super.onCreate()
        onCreated()
    }

    override fun startNativeServices() {
        IndicationOverlay.start()
        MicrophoneOverlay.start()
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
        val glanceId = GlanceAppWidgetManager(this).getGlanceIds(MicrophoneWidget::class.java).firstOrNull()

        if (glanceId != null) {
            MicrophoneWidget().update(this, glanceId)
        }
    }

}