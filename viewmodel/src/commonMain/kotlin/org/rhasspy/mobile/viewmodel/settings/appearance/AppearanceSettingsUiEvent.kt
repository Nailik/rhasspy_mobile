package org.rhasspy.mobile.viewmodel.settings.appearance

import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.theme.ThemeType

sealed interface AppearanceSettingsUiEvent {

    sealed interface Action : AppearanceSettingsUiEvent {

        data object BackClick : Action

    }

    sealed interface Change : AppearanceSettingsUiEvent {

        data class SelectLanguageOption(val option: LanguageType) : Change
        data class SelectThemeOption(val option: ThemeType) : Change

    }
}