package org.rhasspy.mobile.data

import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR

enum class ThemeOptions(override val text: StringResource) : DataEnum {
    System(MR.strings.systemTheme),
    Dark(MR.strings.darkTheme),
    Light(MR.strings.lightTheme)
}