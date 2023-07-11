package org.rhasspy.mobile.platformspecific.language

import org.rhasspy.mobile.data.language.LanguageType

interface ILanguageUtils {

    fun getDeviceLanguage(): LanguageType
    fun setupLanguage(defaultLanguageType: LanguageType): LanguageType
    fun getSystemAppLanguage(): LanguageType?
    fun setLanguage(languageType: LanguageType)

}

internal expect class LanguageUtils() : ILanguageUtils {

    override fun getDeviceLanguage(): LanguageType

    override fun setupLanguage(defaultLanguageType: LanguageType): LanguageType

    override fun getSystemAppLanguage(): LanguageType?

    override fun setLanguage(languageType: LanguageType)

}