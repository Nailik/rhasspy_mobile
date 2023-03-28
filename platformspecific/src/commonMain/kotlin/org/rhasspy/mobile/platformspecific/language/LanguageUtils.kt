package org.rhasspy.mobile.platformspecific.language

import org.rhasspy.mobile.data.language.LanguageType

expect fun getDeviceLanguage(): LanguageType

expect fun setupLanguage(defaultLanguageType: LanguageType) : LanguageType

expect fun getSystemAppLanguage(): LanguageType?

expect fun setLanguage(languageType: LanguageType)