package org.rhasspy.mobile.android

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import co.touchlab.kermit.Logger
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay
import org.rhasspy.mobile.android.widget.MicrophoneWidget
import org.rhasspy.mobile.nativeutils.NativeApplication
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.types.LanguageType
import kotlin.system.exitProcess

/**
 * holds android application and native functions and provides koin module
 */
class AndroidApplication : Application() {
    private val logger = Logger.withTag("AndroidApplication")

    init {
        nativeInstance = this
    }

    companion object {
        lateinit var nativeInstance: NativeApplication
            private set
    }

    init {
        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
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

    override fun restart() {
        try {
            val packageManager: PackageManager = this.packageManager
            val intent: Intent = packageManager.getLaunchIntentForPackage(this.packageName)!!
            val componentName: ComponentName = intent.component!!
            val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
            nativeInstance.startActivity(restartIntent)
            Runtime.getRuntime().exit(0)
        } catch (e: Exception) {
            Runtime.getRuntime().exit(0)
        }
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    override suspend fun updateWidget() {
        GlanceAppWidgetManager(nativeInstance).getGlanceIds(MicrophoneWidget::class.java)
            .firstOrNull()
            ?.also { MicrophoneWidget().update(nativeInstance, it) }
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun isInstrumentedTest(): Boolean {
        return Settings.System.getString(contentResolver, "firebase.test.lab") == "true"
    }


    override fun getDeviceLanguage(): LanguageType {
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        return LocaleListCompat.getDefault().getFirstMatch(arrayOf("en", "de")).let {
            when (it?.language) {
                "en" -> LanguageType.English
                "de" -> LanguageType.German
                else -> LanguageType.English
            }
        }
    }

    override fun getSystemAppLanguage(): LanguageType? {
        return AppCompatDelegate.getApplicationLocales().getFirstMatch(arrayOf("en", "de")).let {
            when (it?.language) {
                "en" -> LanguageType.English
                "de" -> LanguageType.German
                else -> null
            }
        }
    }

    override fun setLanguage(languageType: LanguageType) {
        val appLocale = LocaleListCompat.forLanguageTags(languageType.code)
        AppCompatDelegate.setApplicationLocales(appLocale)
        AppSetting.languageType.value = languageType
    }

}