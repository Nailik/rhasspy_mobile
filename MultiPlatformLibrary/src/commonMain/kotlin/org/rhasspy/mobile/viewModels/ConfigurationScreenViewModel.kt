package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.*

class ConfigurationScreenViewModel  : ViewModel() {

    var siteId = MutableLiveData("")
    var isHttpSSL = MutableLiveData(false)

    var isMqttSSL = MutableLiveData(false)
    var mqttHost = MutableLiveData("")
    var mqttPort = MutableLiveData("")
    var mqttUserName = MutableLiveData("")
    var mqttPassword = MutableLiveData("")

    var isUDPOutput = MutableLiveData(false)
    var udpOutputHost = MutableLiveData("")
    var udpOutputPort = MutableLiveData("")

    var wakeWordValueOption = MutableLiveData(WakeWordOption.Porcupine)
    var wakeWordAccessToken = MutableLiveData("")
    var wakeWordKeyword = MutableLiveData(0f)

    var speechToTextOption = MutableLiveData(SpeechToTextOptions.Disabled)
    var speechToTextHttpEndpoint = MutableLiveData("")

    var intentRecognitionOption = MutableLiveData(IntentRecognitionOptions.Disabled)
    var intentRecognitionEndpoint = MutableLiveData("")

    var textToSpeechOption = MutableLiveData(TextToSpeechOptions.Disabled)
    var textToSpeechEndpoint = MutableLiveData("")

    var audioPlayingOption = MutableLiveData(AudioPlayingOptions.Disabled)
    var audioPlayingEndpoint = MutableLiveData("")

    var dialogueManagementOption = MutableLiveData(DialogueManagementOptions.Disabled)

    var intentHandlingOption = MutableLiveData(IntentHandlingOptions.Disabled)
    var intentHandlingEndpoint = MutableLiveData("")
    var intentHandlingHassUrl = MutableLiveData("")
    var intentHandlingHassAccessToken = MutableLiveData("")
    var isIntentHandlingHassEvent = MutableLiveData(false)
}