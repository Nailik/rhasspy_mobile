package org.rhasspy.mobile.platformspecific.language

import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.language.LanguageType
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

internal actual class LanguageUtils : ILanguageUtils {

    actual override fun getDeviceLanguage(): LanguageType {
        return when (NSLocale.preferredLanguages.firstOrNull()) {
            "en" -> LanguageType.English
            "de" -> LanguageType.German
            else -> LanguageType.English
        }
    }

    actual override fun setupLanguage(defaultLanguageType: LanguageType): LanguageType {
        val language: LanguageType = getSystemAppLanguage() ?: defaultLanguageType
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.code)
        if (getDeviceLanguage() != language && getSystemAppLanguage() != language) {
            //only needs to be set if it differs from current settings and from device settings
            setLanguage(language)
        }
        return language
    }

    actual override fun getSystemAppLanguage(): LanguageType? {
        return when (NSBundle.mainBundle.preferredLocalizations.firstOrNull()) {
            "en" -> LanguageType.English
            "de" -> LanguageType.German
            else -> LanguageType.English
        }
    }

    actual override fun setLanguage(languageType: LanguageType) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(languageType.code)
        NSUserDefaults.standardUserDefaults().setObject(languageType.code, "i18n_language")
        NSUserDefaults.standardUserDefaults().synchronize()
    }

}