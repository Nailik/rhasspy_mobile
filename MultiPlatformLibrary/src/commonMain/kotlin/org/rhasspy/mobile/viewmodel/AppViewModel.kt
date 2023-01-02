package org.rhasspy.mobile.viewmodel

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.types.LanguageType
import org.rhasspy.mobile.settings.AppSetting

/**
 * holds information used by various ui items
 */
class AppViewModel {

    val languageType: StateFlow<LanguageType> get() = AppSetting.languageType.data

}