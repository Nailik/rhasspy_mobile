package org.rhasspy.mobile.viewmodel.settings.language

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.toImmutableList

data class LanguageSettingsViewState(
    val languageOption: LanguageType = AppSetting.languageType.value
) {
    val languageOptions: ImmutableList<LanguageType> get() = LanguageType.values().toImmutableList()
}