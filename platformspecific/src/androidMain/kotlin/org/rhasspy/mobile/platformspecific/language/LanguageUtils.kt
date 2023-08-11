package org.rhasspy.mobile.platformspecific.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.language.LanguageType

internal actual class LanguageUtils : ILanguageUtils {

    actual override fun getDeviceLanguage(): LanguageType {
        return when (LocaleListCompat.getDefault().getFirstMatch(arrayOf("en", "de"))?.language) {
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
        return AppCompatDelegate.getApplicationLocales().getFirstMatch(arrayOf("en", "de")).let {
            when (it?.language) {
                "en" -> LanguageType.English
                "de" -> LanguageType.German
                else -> null
            }
        }
    }

    actual override fun setLanguage(languageType: LanguageType) {
        val appLocale = LocaleListCompat.forLanguageTags(languageType.code)
        CoroutineScope(Dispatchers.Main).launch {
            //must be called from main thread
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
        StringDesc.localeType = StringDesc.LocaleType.Custom(languageType.code)
    }

}