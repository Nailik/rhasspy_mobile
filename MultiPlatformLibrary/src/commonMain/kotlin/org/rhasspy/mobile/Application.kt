package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.mqtt.OverlayServices
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.ServiceAction
import org.rhasspy.mobile.services.ServiceInterface
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

        //initialize/load the settings, generate the MutableStateFlow
        AppSettings
        ConfigurationSettings
        OverlayServices.checkPermission()
        startNativeServices()
        //makes sure that the MutableStateFlow inside those objects are created in ui thread because they internally use livedata which cannot be
        // created in background tread
        ServiceInterface
        MqttService

        CoroutineScope(Dispatchers.Default).launch {
            ServiceInterface.serviceAction(ServiceAction.Start)
        }
    }

}