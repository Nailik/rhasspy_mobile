package org.rhasspy.mobile.viewmodel.settings.language

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.language.setLanguage
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SelectLanguageOption

@Stable
class LanguageSettingsViewModel : KViewModel() {

    private val _viewState = MutableStateFlow(LanguageSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: LanguageSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SelectLanguageOption -> {
                    AppSetting.languageType.value = change.option
                    setLanguage(change.option)
                    it.copy(languageOption = change.option)
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is BackClick -> navigator.onBackPressed()
        }
    }

}