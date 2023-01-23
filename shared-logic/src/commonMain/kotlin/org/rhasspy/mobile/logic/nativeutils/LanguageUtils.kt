package org.rhasspy.mobile.logic.nativeutils

import org.rhasspy.mobile.logic.settings.types.LanguageType

expect fun isDebug(): Boolean

expect fun getDeviceLanguage(): LanguageType

expect fun setupLanguage()

expect fun getSystemAppLanguage(): LanguageType?

expect fun setLanguage(languageType: LanguageType)