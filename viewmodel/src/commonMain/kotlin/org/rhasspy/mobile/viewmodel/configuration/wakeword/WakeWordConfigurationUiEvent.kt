package org.rhasspy.mobile.viewmodel.configuration.wakeword

import okio.Path
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination

sealed interface WakeWordConfigurationUiEvent {

    sealed interface Change : WakeWordConfigurationUiEvent {

        data class SelectWakeWordOption(val option: WakeWordOption) : Change

    }

    sealed interface Action : WakeWordConfigurationUiEvent {

        data object RequestMicrophonePermission : Action
        data object OpenAudioRecorderSettings : Action
        data object BackClick : Action
        data class Navigate(val destination: WakeWordConfigurationScreenDestination) : Action

    }

    sealed interface PorcupineUiEvent : WakeWordConfigurationUiEvent {

        sealed interface Change : PorcupineUiEvent {

            data class UpdateWakeWordPorcupineAccessToken(val value: String) : Change
            data class SelectWakeWordPorcupineLanguage(val option: PorcupineLanguageOption) : Change
            data class UpdateWakeWordPorcupineKeywordDefaultSensitivity(
                val item: PorcupineDefaultKeyword,
                val value: Float
            ) : Change

            data class ClickPorcupineKeywordDefault(val item: PorcupineDefaultKeyword) : Change
            data class SetPorcupineKeywordDefault(
                val item: PorcupineDefaultKeyword,
                val value: Boolean
            ) : Change

            data class UpdateWakeWordPorcupineKeywordCustomSensitivity(
                val item: PorcupineCustomKeyword,
                val value: Float
            ) : Change

            data class ClickPorcupineKeywordCustom(val item: PorcupineCustomKeyword) : Change
            data class SetPorcupineKeywordCustom(
                val item: PorcupineCustomKeyword,
                val value: Boolean
            ) : Change

            data class UndoCustomKeywordDeleted(val item: PorcupineCustomKeyword) : Change
            data class AddPorcupineKeywordCustom(val path: Path) : Change
            data class DeletePorcupineKeywordCustom(val item: PorcupineCustomKeyword) : Change
            data class SetPorcupineAudioRecorderSettings(val enabled: Boolean) : Change
        }

        sealed interface Action : PorcupineUiEvent {

            data object DownloadCustomPorcupineKeyword : Action
            data object AddCustomPorcupineKeyword : Action
            data object OpenPicoVoiceConsole : Action
            data object BackClick : Action
            data object PorcupineLanguageClick : Action
            data class PageClick(val screen: Int) : Action

        }
    }

    sealed interface UdpUiEvent : WakeWordConfigurationUiEvent {

        sealed interface Change : UdpUiEvent {

            data class UpdateUdpOutputHost(val value: String) : Change
            data class UpdateUdpOutputPort(val value: String) : Change
        }


    }

}