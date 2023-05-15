package org.rhasspy.mobile.viewmodel.settings.log

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Navigate.BackClick

@Stable
class LogSettingsViewModel(
    private val nativeApplication: NativeApplication,
    private val navigator: Navigator
) : ViewModel(), KoinComponent {

    private val _viewState = MutableStateFlow(LogSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: LogSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SetCrashlyticsEnabled -> {
                    AppSetting.isCrashlyticsEnabled.value = change.enabled
                    nativeApplication.setCrashlyticsCollectionEnabled(change.enabled)
                    it.copy(isCrashlyticsEnabled = change.enabled)
                }

                is SetLogAudioFramesEnabled -> {
                    AppSetting.isLogAudioFramesEnabled.value = change.enabled
                    it.copy(isLogAudioFramesEnabled = change.enabled)
                }

                is SetLogLevel -> {
                    AppSetting.logLevel.value = change.logLevel
                    Logger.setMinSeverity(AppSetting.logLevel.value.severity)
                    it.copy(logLevel = change.logLevel)
                }

                is SetShowLogEnabled -> {
                    AppSetting.isShowLogEnabled.value = change.enabled
                    it.copy(isShowLogEnabled = change.enabled)
                }
            }
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            is BackClick -> navigator.popBackStack()
        }
    }

}