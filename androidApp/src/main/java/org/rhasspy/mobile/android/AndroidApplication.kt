package org.rhasspy.mobile.android

import androidx.glance.appwidget.GlanceAppWidgetManager
import co.touchlab.kermit.Logger
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay
import org.rhasspy.mobile.android.widget.MicrophoneWidget
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import kotlin.system.exitProcess

/**
 * holds android application and native functions and provides koin module
 */
class AndroidApplication : Application(), KoinComponent {
    private val logger = Logger.withTag("AndroidApplication")

    init {
        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            println(exception)
            logger.a(exception) { "uncaught exception in Thread $thread" }
            exitProcess(2)
        }
    }

    override fun onCreate() {
        super.onCreate()
        onCreated()
    }

    override fun startOverlay() {
        CoroutineScope(Dispatchers.Main).launch {
            //lifecycle must be called on main thread
            IndicationOverlay.start()
            MicrophoneOverlay.start()
        }
    }

    override fun stopOverlay() {
        CoroutineScope(Dispatchers.Main).launch {
            IndicationOverlay.stop()
            MicrophoneOverlay.stop()
        }
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    override suspend fun updateWidget() {
        val context = get<NativeApplication>()
        GlanceAppWidgetManager(context).getGlanceIds(MicrophoneWidget::class.java)
            .firstOrNull()
            ?.also { MicrophoneWidget().update(context, it) }
    }

}