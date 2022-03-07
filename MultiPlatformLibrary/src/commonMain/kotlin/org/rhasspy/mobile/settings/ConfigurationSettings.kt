package org.rhasspy.mobile.settings

import org.rhasspy.mobile.data.*

object ConfigurationSettings {

    val siteId = Setting(SettingsEnum.SiteId, "")

    val isHttpSSL = Setting(SettingsEnum.HttpSSL, false)

    val isMqttSSL = Setting(SettingsEnum.MQTT_SSL, false)
    val mqttHost = Setting(SettingsEnum.MQTTHost, "")
    val mqttPort = Setting(SettingsEnum.MQTTPort, "")
    val mqttUserName = Setting(SettingsEnum.MQTTUserName, "")
    val mqttPassword = Setting(SettingsEnum.MQTTPassword, "")

    val isUDPOutput = Setting(SettingsEnum.UDPOutput, false)
    val udpOutputHost = Setting(SettingsEnum.UDPOutputHost, "")
    val udpOutputPort = Setting(SettingsEnum.UDPOutputPort, "")

    val wakeWordOption = Setting(SettingsEnum.WakeWordOption, WakeWordOption.Porcupine)
    val wakeWordAccessToken = Setting(SettingsEnum.WakeWordAccessToken, "")
    val wakeWordKeyword = Setting(SettingsEnum.WakeWordKeyword, 0f)

    val speechToTextOption = Setting(SettingsEnum.SpeechToTextOption, SpeechToTextOptions.Disabled)
    val speechToTextHttpEndpoint = Setting(SettingsEnum.SpeechToTextHttpEndpoint, "")

    val intentRecognitionOption = Setting(SettingsEnum.IntentRecognitionOption, IntentRecognitionOptions.Disabled)
    val intentRecognitionEndpoint = Setting(SettingsEnum.IntentRecognitionEndpoint, "")

    val textToSpeechOption = Setting(SettingsEnum.TextToSpeechOption, TextToSpeechOptions.Disabled)
    val textToSpeechEndpoint = Setting(SettingsEnum.TextToSpeechEndpoint, "")

    val audioPlayingOption = Setting(SettingsEnum.AudioPlayingOption, AudioPlayingOptions.Disabled)
    val audioPlayingEndpoint = Setting(SettingsEnum.AudioPlayingEndpoint, "")

    val dialogueManagementOption = Setting(SettingsEnum.DialogueManagementOption, DialogueManagementOptions.Disabled)

    val intentHandlingOption = Setting(SettingsEnum.IntentHandlingOption, IntentHandlingOptions.Disabled)
    val intentHandlingEndpoint = Setting(SettingsEnum.IntentHandlingEndpoint, "")
    val intentHandlingHassUrl = Setting(SettingsEnum.IntentHandlingHassUrl, "")
    val intentHandlingHassAccessToken = Setting(SettingsEnum.IntentHandlingHassAccessToken, "")
    val isIntentHandlingHassEvent = Setting(SettingsEnum.IsIntentHandlingHassEvent, false)

}
