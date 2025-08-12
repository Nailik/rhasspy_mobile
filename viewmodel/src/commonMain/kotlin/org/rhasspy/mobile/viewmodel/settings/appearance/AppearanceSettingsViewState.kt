package org.rhasspy.mobile.viewmodel.settings.appearance

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.settings.AppSetting

@Stable
data class AppearanceSettingsViewState(
    val languageOption: LanguageType = AppSetting.languageType.value,
    val themeOption: ThemeType = AppSetting.themeType.value,
) {
    val languageOptions: ImmutableList<LanguageType> = LanguageType.entries.toImmutableList()
    val themeOptions: ImmutableList<ThemeType> = ThemeType.entries.toImmutableList()
}