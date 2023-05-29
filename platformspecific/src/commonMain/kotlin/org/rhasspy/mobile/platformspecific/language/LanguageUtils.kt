package org.rhasspy.mobile.platformspecific.language

import org.rhasspy.mobile.data.language.LanguageType

expect class LanguageUtils() {

    fun getDeviceLanguage(): LanguageType

    fun setupLanguage(defaultLanguageType: LanguageType): LanguageType

    fun getSystemAppLanguage(): LanguageType?

    fun setLanguage(languageType: LanguageType)

}