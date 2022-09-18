package org.rhasspy.mobile.android

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.livedata.readOnly
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.NativeApplication
import org.rhasspy.mobile.android.uiservices.IndicationOverlay
import org.rhasspy.mobile.android.uiservices.MicrophoneOverlay


class AndroidApplication : Application() {

    init {
        Instance = this
    }

    companion object {
        lateinit var Instance: NativeApplication
            private set

        private val currentlyAppInBackground = MutableLiveData(false)
        val isAppInBackground = currentlyAppInBackground.readOnly()
    }

    init {
        //catches all exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Logger.withTag("AndroidApplication").e(exception) {
                "uncaught exception in Thread $thread"
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> currentlyAppInBackground.postValue(false)
                    Lifecycle.Event.ON_STOP -> currentlyAppInBackground.postValue(true)
                    else -> {}
                }
            }
        })

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

}