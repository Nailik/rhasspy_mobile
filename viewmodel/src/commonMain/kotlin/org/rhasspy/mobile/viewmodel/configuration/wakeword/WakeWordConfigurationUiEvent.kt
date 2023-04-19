package org.rhasspy.mobile.viewmodel.configuration.wakeword

import okio.Path
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

sealed interface WakeWordConfigurationUiEvent {

    sealed interface Change : WakeWordConfigurationUiEvent {
        data class SelectWakeWordOption(val option: WakeWordOption) : Change
    }

    sealed interface Action : WakeWordConfigurationUiEvent {
        object MicrophonePermissionAllowed : Action
        object TestStartWakeWord : Action
    }

    sealed interface PorcupineUiEvent : WakeWordConfigurationUiEvent {

        sealed interface Change : PorcupineUiEvent {
            data class UpdateWakeWordPorcupineAccessToken(val value: String) : Change
            data class SelectWakeWordPorcupineLanguage(val option: PorcupineLanguageOption) : Change
            data class UpdateWakeWordPorcupineKeywordDefaultSensitivity(val index: Int, val value: Float) : Change
            data class ClickPorcupineKeywordDefault(val index: Int) : Change
            data class SetPorcupineKeywordDefault(val index: Int, val value: Boolean) : Change
            data class UpdateWakeWordPorcupineKeywordCustomSensitivity(val index: Int, val value: Float) : Change
            data class ClickPorcupineKeywordCustom(val index: Int) : Change
            data class SetPorcupineKeywordCustom(val index: Int, val value: Boolean) : Change
            data class UndoCustomKeywordDeleted(val index: Int) : Change
            data class AddPorcupineKeywordCustom(val path: Path) : Change
            data class DeletePorcupineKeywordCustom(val index: Int) : Change
        }

        sealed interface Action : PorcupineUiEvent {
            object DownloadCustomPorcupineKeyword : Action
            object AddCustomPorcupineKeyword : Action
            object OpenPicoVoiceConsole : Action
        }
    }

    sealed interface UdpUiEvent : WakeWordConfigurationUiEvent {
        sealed interface Change : UdpUiEvent {
            data class UpdateUdpOutputHost(val value: String) : Change
            data class UpdateUdpOutputPort(val value: String) : Change
        }

    }
}