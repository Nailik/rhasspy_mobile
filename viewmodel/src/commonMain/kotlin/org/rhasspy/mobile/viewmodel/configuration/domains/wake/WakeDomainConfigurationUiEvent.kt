package org.rhasspy.mobile.viewmodel.configuration.domains.wake

import okio.Path
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeDomainOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination

sealed interface WakeDomainConfigurationUiEvent {

    sealed interface Change : WakeDomainConfigurationUiEvent {

        data class SelectWakeDomainOption(val option: WakeDomainOption) : Change

    }

    sealed interface Action : WakeDomainConfigurationUiEvent {

        data class Navigate(val destination: WakeWordConfigurationScreenDestination) : Action

    }

    sealed interface PorcupineUiEvent : WakeDomainConfigurationUiEvent {

        sealed interface Change : PorcupineUiEvent {

            data class UpdateWakeDomainPorcupineAccessToken(val value: String) : Change
            data class SelectWakeDomainPorcupineLanguage(val option: PorcupineLanguageOption) : Change
            data class UpdateWakeDomainPorcupineKeywordDefaultSensitivity(
                val item: PorcupineDefaultKeyword,
                val value: Double
            ) : Change

            data class ClickPorcupineKeywordDefault(val item: PorcupineDefaultKeyword) : Change
            data class SetPorcupineKeywordDefault(
                val item: PorcupineDefaultKeyword,
                val value: Boolean
            ) : Change

            data class UpdateWakeDomainPorcupineKeywordCustomSensitivity(
                val item: PorcupineCustomKeyword,
                val value: Double
            ) : Change

            data class ClickPorcupineKeywordCustom(val item: PorcupineCustomKeyword) : Change
            data class SetPorcupineKeywordCustom(
                val item: PorcupineCustomKeyword,
                val value: Boolean
            ) : Change

            data class AddPorcupineKeywordCustom(val path: Path) : Change
            data class DeletePorcupineKeywordCustom(val item: PorcupineCustomKeyword) : Change

        }

        sealed interface Action : PorcupineUiEvent {

            data object DownloadCustomPorcupineKeyword : Action
            data object AddCustomPorcupineKeyword : Action
            data object OpenPicoVoiceConsole : Action
            data object PorcupineLanguageClick : Action
            data class PageClick(val screen: Int) : Action

        }

    }

    sealed interface UdpUiEvent : WakeDomainConfigurationUiEvent {

        sealed interface Change : UdpUiEvent {

            data class UpdateUdpOutputHost(val value: String) : Change
            data class UpdateUdpOutputPort(val value: String) : Change

        }

    }

}