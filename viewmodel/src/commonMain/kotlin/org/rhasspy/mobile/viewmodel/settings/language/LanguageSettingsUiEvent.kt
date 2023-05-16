package org.rhasspy.mobile.viewmodel.settings.language

import org.rhasspy.mobile.data.language.LanguageType

sealed interface LanguageSettingsUiEvent {

    sealed interface Navigate : LanguageSettingsUiEvent {
        object BackClick : Navigate
    }

    sealed interface Change : LanguageSettingsUiEvent {

        data class SelectLanguageOption(val option: LanguageType) : Change

    }
}