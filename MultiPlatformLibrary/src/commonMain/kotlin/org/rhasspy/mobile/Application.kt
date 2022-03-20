package org.rhasspy.mobile

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.RecordingService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

abstract class Application : NativeApplication() {
    private val logger = Logger.withTag(Application::class.simpleName!!)

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
        MqttService
        RecordingService
        ConfigurationSettings
        ServiceInterface
        ForegroundService
    }

}