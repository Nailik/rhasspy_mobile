package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.data.*

object ConfigurationData {

    val siteId = MutableLiveData("")
    val isHttpSSL = MutableLiveData(false)

    val isMqttSSL = MutableLiveData(false)
    val mqttHost = MutableLiveData("")
    val mqttPort = MutableLiveData("")
    val mqttUserName = MutableLiveData("")
    val mqttPassword = MutableLiveData("")

    val isUDPOutput = MutableLiveData(false)
    val udpOutputHost = MutableLiveData("")
    val udpOutputPort = MutableLiveData("")

    val wakeWordValueOption = MutableLiveData(WakeWordOption.Porcupine)
    val wakeWordAccessToken = MutableLiveData("")
    val wakeWordKeyword = MutableLiveData(0f)

    val speechToTextOption = MutableLiveData(SpeechToTextOptions.Disabled)
    val speechToTextHttpEndpoint = MutableLiveData("")

    val intentRecognitionOption = MutableLiveData(IntentRecognitionOptions.Disabled)
    val intentRecognitionEndpoint = MutableLiveData("")

    val textToSpeechOption = MutableLiveData(TextToSpeechOptions.Disabled)
    val textToSpeechEndpoint = MutableLiveData("")

    val audioPlayingOption = MutableLiveData(AudioPlayingOptions.Disabled)
    val audioPlayingEndpoint = MutableLiveData("")

    val dialogueManagementOption = MutableLiveData(DialogueManagementOptions.Disabled)

    val intentHandlingOption = MutableLiveData(IntentHandlingOptions.Disabled)
    val intentHandlingEndpoint = MutableLiveData("")
    val intentHandlingHassUrl = MutableLiveData("")
    val intentHandlingHassAccessToken = MutableLiveData("")
    val isIntentHandlingHassEvent = MutableLiveData(false)

}
