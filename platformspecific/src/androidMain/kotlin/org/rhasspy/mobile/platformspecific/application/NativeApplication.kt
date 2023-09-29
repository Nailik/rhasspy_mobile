package org.rhasspy.mobile.platformspecific.application

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy.Builder
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.utils.isDebug
import kotlin.system.exitProcess


@OptIn(ExperimentalMultiplatform::class)
//@AllowDifferentMembersInActual
actual abstract class NativeApplication : AppApplication(), KoinComponent {
    private val logger = Logger.withTag("AndroidApplication")

    init {
        onInit()
    }

    var currentActivity: AppCompatActivity? = null
        private set

    actual companion object {
        private lateinit var koinApplicationInstance: NativeApplication
        actual val koinApplicationModule = module {
            single { koinApplicationInstance }
        }
    }

    actual fun onInit() {
        koinApplicationInstance = this
    }

    actual abstract fun onCreated()

    override fun onCreate() {
        super.onCreate()

        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            logger.a(exception) { "uncaught exception in Thread $thread" }
            exitProcess(2)
        }
        if (isDebug()) {
            try {
                StrictMode.setVmPolicy(
                    Builder(StrictMode.getVmPolicy()).detectAll().detectAll().build()
                )
            } catch (_: Exception) {
            }
        }

        onCreated()

        CoroutineScope(get<IDispatcherProvider>().Main).launch {
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_START  -> currentlyAppInBackground.value = false
                        Lifecycle.Event.ON_STOP   -> currentlyAppInBackground.value = true
                        Lifecycle.Event.ON_RESUME -> {
                            CoroutineScope(get<IDispatcherProvider>().IO).launch {
                                resume()
                            }
                        }

                        else                      -> Unit
                    }
                }
            })
        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
            override fun onActivityStarted(p0: Activity) {
                //always represents top activity
                if (p0 is IMainActivity) {
                    currentActivity = p0
                    (get<IExternalResultRequest>() as ExternalResultRequest).registerCallback(p0)
                }
            }

            override fun onActivityResumed(p0: Activity) {}
            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityDestroyed(p0: Activity) {}
        })
    }

    actual val currentlyAppInBackground = MutableStateFlow(false)
    actual val isAppInBackground: StateFlow<Boolean>
        get() = currentlyAppInBackground

    actual fun isInstrumentedTest(): Boolean {
        return Settings.System.getString(contentResolver, "firebase.test.lab") == "true"
    }

    actual fun restart() {
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

    actual abstract val isHasStarted: StateFlow<Boolean>
    actual abstract suspend fun resume()
    actual fun closeApp() {
        currentActivity?.moveTaskToBack(false)
    }

}