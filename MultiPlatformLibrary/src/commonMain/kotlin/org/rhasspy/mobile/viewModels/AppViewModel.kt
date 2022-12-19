package org.rhasspy.mobile.viewModels

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.settings.AppSettings

//TODO koin
/**
 * holds information used by various ui items
 */
object AppViewModel {

    val languageOption: StateFlow<LanguageOptions> get() = AppSettings.languageOption.data

}