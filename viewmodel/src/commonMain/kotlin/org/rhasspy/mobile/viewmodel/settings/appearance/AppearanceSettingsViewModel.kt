package org.rhasspy.mobile.viewmodel.settings.appearance

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.language.ILanguageUtils
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change.SelectLanguageOption
import org.rhasspy.mobile.viewmodel.settings.appearance.AppearanceSettingsUiEvent.Change.SelectThemeOption

@Stable
class AppearanceSettingsViewModel(
    private val languageUtils: ILanguageUtils
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(AppearanceSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: AppearanceSettingsUiEvent) {
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
                    languageUtils.setLanguage(change.option)
                    it.copy(languageOption = change.option)
                }

                is SelectThemeOption    -> {
                    AppSetting.themeType.value = change.option
                    it.copy(themeOption = change.option)
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