package org.rhasspy.mobile.logic.nativeutils

import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.types.LanguageType

actual fun getDeviceLanguage(): LanguageType {
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    return LocaleListCompat.getDefault().getFirstMatch(arrayOf("en", "de")).let {
        when (it?.language) {
            "en" -> LanguageType.English
            "de" -> LanguageType.German
            else -> LanguageType.English
        }
    }
}

actual fun setupLanguage() {
    val language: LanguageType = getSystemAppLanguage() ?: AppSetting.languageType.value
    StringDesc.localeType = StringDesc.LocaleType.Custom(language.code)
    AppSetting.languageType.value = language
    if(getDeviceLanguage() != language && getSystemAppLanguage() != language) {
        //only needs to be set if it differs from current settings and from device settings
        setLanguage(language)
    }
}

actual fun getSystemAppLanguage(): LanguageType? {
    return AppCompatDelegate.getApplicationLocales().getFirstMatch(arrayOf("en", "de")).let {
        when (it?.language) {
            "en" -> LanguageType.English
            "de" -> LanguageType.German
            else -> null
        }
    }
}

actual fun setLanguage(languageType: LanguageType) {
    val appLocale = LocaleListCompat.forLanguageTags(languageType.code)
    CoroutineScope(Dispatchers.Main).launch {
        //must be called from main thread
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
    StringDesc.localeType = StringDesc.LocaleType.Custom(languageType.code)
    AppSetting.languageType.value = languageType
}