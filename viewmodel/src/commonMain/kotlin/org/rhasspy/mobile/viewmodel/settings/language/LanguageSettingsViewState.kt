package org.rhasspy.mobile.viewmodel.settings.language

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class LanguageSettingsViewState internal constructor(
    val languageOption: LanguageType = AppSetting.languageType.value
) {
    val languageOptions: ImmutableList<LanguageType> = LanguageType.values().toImmutableList()
}