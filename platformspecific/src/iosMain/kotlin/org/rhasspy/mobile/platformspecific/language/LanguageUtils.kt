package org.rhasspy.mobile.platformspecific.language

import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.language.LanguageType

actual class LanguageUtils {

    actual fun getDeviceLanguage(): LanguageType {
        //TODO("Not yet implemented")
        return LanguageType.English
    }

    actual fun setupLanguage(defaultLanguageType: LanguageType): LanguageType {
        //TODO("Not yet implemented")
        return LanguageType.English
    }

    actual fun getSystemAppLanguage(): LanguageType? {
        //TODO("Not yet implemented")
        return LanguageType.English
    }

    actual fun setLanguage(languageType: LanguageType) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(languageType.code)
    }

}