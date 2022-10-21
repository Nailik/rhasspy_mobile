package org.rhasspy.mobile.viewModels

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.AppSettings

/**
 * holds information used by various ui items
 */
object AppViewModel {

    val themeOption: StateFlow<ThemeOptions> get() = AppSettings.themeOption.data
    val languageOption: StateFlow<LanguageOptions> get() = AppSettings.languageOption.data

}