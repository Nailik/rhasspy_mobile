package org.rhasspy.mobile.viewmodel.settings.appearance

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.AppSetting

@Stable
data class AppearanceSettingsViewState internal constructor(
    val languageOption: LanguageType = AppSetting.languageType.value,
    val themeOption: ThemeType = AppSetting.themeType.value,
) {
    val languageOptions: ImmutableList<LanguageType> = LanguageType.values().toImmutableList()
    val themeOptions: ImmutableList<ThemeType> = ThemeType.values().toImmutableList()
}