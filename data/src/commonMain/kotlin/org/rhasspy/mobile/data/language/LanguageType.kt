package org.rhasspy.mobile.data.language

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

@Serializable
enum class LanguageType(override val text: StableStringResource, val code: String) : IOption {

    English(MR.strings.en.stable, "en"),
    German(MR.strings.de.stable, "de");


}