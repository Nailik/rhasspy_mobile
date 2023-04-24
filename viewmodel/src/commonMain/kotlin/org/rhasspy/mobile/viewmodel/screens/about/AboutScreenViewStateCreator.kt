package org.rhasspy.mobile.viewmodel.screens.about

import com.mikepenz.aboutlibraries.Libs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.libraries.stable
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.resource.readToString

class AboutScreenViewStateCreator(
    val nativeApplication: NativeApplication
) {

    operator fun invoke(): StateFlow<AboutScreenViewState> {
        return MutableStateFlow(
            AboutScreenViewState(
                changelog = BuildKonfig.changelog.split("\\\\")
                    .map { it.replace("\n", "") }
                    .filter { it.isNotEmpty() }
                    .map { "· $it" }
                    .toImmutableList(),
                privacy = MR.files.dataprivacy.readToString(nativeApplication),
                libraries = Libs.Builder().withJson(
                    MR.files.aboutlibraries.readToString(nativeApplication)
                ).build().libraries.map {
                    it.stable
                }.toImmutableList()
            )
        )

    }

}