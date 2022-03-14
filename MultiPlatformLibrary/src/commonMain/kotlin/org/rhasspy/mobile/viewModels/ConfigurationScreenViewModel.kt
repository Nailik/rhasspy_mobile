package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.services.MqttService

class ConfigurationScreenViewModel : ViewModel() {

    val isMQTTConnected = MqttService.isConnected

}