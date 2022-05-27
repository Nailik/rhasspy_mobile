package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.handler.ForegroundServiceHandler
import org.rhasspy.mobile.mqtt.OverlayServices
import org.rhasspy.mobile.services.*
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

abstract class Application : NativeApplication() {
    private val logger = Logger.withTag("Application")


    companion object {
        lateinit var Instance: NativeApplication
            private set
    }

    init {
        @Suppress("LeakingThis")
        Instance = this
    }

    fun onCreated() {
        Logger.addLogWriter(FileLogger)

        logger.a { "######## Application started ########" }

        //initialize/load the settings, generate the MutableLiveData
        AppSettings
        ConfigurationSettings
        OverlayServices.checkPermission()
        startNativeServices()
        CoroutineScope(Dispatchers.Default).launch {
            ServiceInterface.serviceAction(ServiceAction.Start)
        }
    }

}