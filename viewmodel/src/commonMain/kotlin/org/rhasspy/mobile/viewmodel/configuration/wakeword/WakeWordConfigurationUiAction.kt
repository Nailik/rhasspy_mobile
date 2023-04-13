package org.rhasspy.mobile.viewmodel.configuration.wakeword

import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel.PorcupineCustomKeywordUi

sealed interface WakeWordConfigurationUiAction {

    sealed interface Change : WakeWordConfigurationUiAction {
        data class SelectWakeWordOption(val option: WakeWordOption) : Change
        data class UpdateWakeWordPorcupineAccessToken(val value: String) : Change
    }

    sealed interface Navigate: WakeWordConfigurationUiAction {
        object OpenPicoVoiceConsole: Navigate
    }

    sealed interface PorcupineUiAction: WakeWordConfigurationUiAction {
        data class SelectWakeWordPorcupineLanguage(val option: PorcupineLanguageOption) : PorcupineUiAction
        data class UpdateWakeWordPorcupineKeywordDefaultSensitivity(val option: PorcupineDefaultKeyword, val value: Float) : PorcupineUiAction
        data class ClickPorcupineKeywordDefault(val option: PorcupineDefaultKeyword) : PorcupineUiAction
        data class TogglePorcupineKeywordDefault(val option: PorcupineDefaultKeyword, val value: Boolean) : PorcupineUiAction
        data class UpdateWakeWordPorcupineKeywordCustomSensitivity(val option: PorcupineCustomKeyword, val sensitivity: Float) : PorcupineUiAction
        data class ClickPorcupineKeywordCustom(val option: PorcupineCustomKeyword) : PorcupineUiAction
        data class TogglePorcupineKeywordCustom(val option: PorcupineCustomKeyword, val enabled: Boolean) : PorcupineUiAction
        data class UndoCustomKeywordDeleted(val option: PorcupineCustomKeywordUi): PorcupineUiAction
        data class DeletePorcupineKeywordCustom(val option: PorcupineCustomKeywordUi): PorcupineUiAction
        object DownloadCustomPorcupineKeyword: PorcupineUiAction
        object AddCustomPorcupineKeyword: PorcupineUiAction
    }

    sealed interface UdpUiAction: WakeWordConfigurationUiAction {
        data class UpdateUdpOutputHost(val value: String) : UdpUiAction
        data class UpdateUdpOutputPort(val value: String) : UdpUiAction
    }
}