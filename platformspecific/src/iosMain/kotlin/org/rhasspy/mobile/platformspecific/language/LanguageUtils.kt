package org.rhasspy.mobile.platformspecific.language

import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.language.LanguageType

internal actual class LanguageUtils : ILanguageUtils {

    actual override fun getDeviceLanguage(): LanguageType {
        //TODO("Not yet implemented")
        return LanguageType.English
    }

    actual override fun setupLanguage(defaultLanguageType: LanguageType): LanguageType {
        //TODO("Not yet implemented")
        return LanguageType.English
    }

    actual override fun getSystemAppLanguage(): LanguageType? {
        //TODO("Not yet implemented")
        return LanguageType.English
    }

    actual override fun setLanguage(languageType: LanguageType) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(languageType.code)
    }

}