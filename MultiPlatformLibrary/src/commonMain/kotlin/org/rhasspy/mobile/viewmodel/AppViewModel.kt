package org.rhasspy.mobile.viewmodel

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.settings.AppSettings

/**
 * holds information used by various ui items
 */
class AppViewModel {

    val languageOption: StateFlow<LanguageOptions> get() = AppSettings.languageOption.data

}