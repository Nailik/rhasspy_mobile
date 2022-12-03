package org.rhasspy.mobile.android

import android.content.Intent
import co.touchlab.kermit.Logger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.NativeApplication
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay
import org.rhasspy.mobile.viewModels.HomeScreenViewModel


class AndroidApplication : Application() {

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
            Logger.withTag("AndroidApplication").e(exception) {
                "uncaught exception in Thread $thread"
            }
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

    override val viewModelModule: Module
        get() = module {
            viewModelOf(::HomeScreenViewModel)
        }

}