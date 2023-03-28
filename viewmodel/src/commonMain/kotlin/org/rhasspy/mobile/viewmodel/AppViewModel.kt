package org.rhasspy.mobile.viewmodel

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.logic.settings.AppSetting

/**
 * holds information used by various ui items
 */
class AppViewModel {

    val languageType: StateFlow<LanguageType> get() = AppSetting.languageType.data

}