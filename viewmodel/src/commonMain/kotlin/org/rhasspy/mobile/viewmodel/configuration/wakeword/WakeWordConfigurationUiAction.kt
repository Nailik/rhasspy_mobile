package org.rhasspy.mobile.viewmodel.configuration.wakeword

import okio.Path
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

sealed interface WakeWordConfigurationUiAction {

    sealed interface Change : WakeWordConfigurationUiAction {
        data class SelectWakeWordOption(val option: WakeWordOption) : Change
    }

    sealed interface Navigate : WakeWordConfigurationUiAction {
        object MicrophonePermissionAllowed : Navigate
    }

    sealed interface PorcupineUiAction : WakeWordConfigurationUiAction {

        sealed interface Change : PorcupineUiAction {
            data class UpdateWakeWordPorcupineAccessToken(val value: String) : Change
            data class SelectWakeWordPorcupineLanguage(val option: PorcupineLanguageOption) : Change
            data class UpdateWakeWordPorcupineKeywordDefaultSensitivity(val index: Int, val value: Float) : Change
            data class ClickPorcupineKeywordDefault(val index: Int) : Change
            data class TogglePorcupineKeywordDefault(val index: Int, val value: Boolean) : Change
            data class UpdateWakeWordPorcupineKeywordCustomSensitivity(val index: Int, val value: Float) : Change
            data class ClickPorcupineKeywordCustom(val index: Int) : Change
            data class TogglePorcupineKeywordCustom(val index: Int, val value: Boolean) : Change
            data class UndoCustomKeywordDeleted(val index: Int) : Change
            data class AddPorcupineKeywordCustom(val path: Path) : Change
            data class DeletePorcupineKeywordCustom(val index: Int) : Change
        }

        sealed interface Navigate : PorcupineUiAction {
            object DownloadCustomPorcupineKeyword : Navigate
            object AddCustomPorcupineKeyword : Navigate
            object OpenPicoVoiceConsole : Navigate
        }
    }

    sealed interface UdpUiAction : WakeWordConfigurationUiAction {
        sealed interface Change : UdpUiAction {
            data class UpdateUdpOutputHost(val value: String) : Change
            data class UpdateUdpOutputPort(val value: String) : Change
        }

    }
}