package org.rhasspy.mobile.viewmodel.screens.about

import com.mikepenz.aboutlibraries.Libs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.data.libraries.stable
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.resource.readToString
import org.rhasspy.mobile.resources.MR

class AboutScreenViewStateCreator(
    val nativeApplication: INativeApplication
) {

    operator fun invoke(): MutableStateFlow<AboutScreenViewState> {
        return MutableStateFlow(
            AboutScreenViewState(
                changelog = BuildKonfig.changelog.split("\\\\")
                    .map { it.replace("\n", "") }
                    .filter { it.isNotEmpty() }
                    .map { "· $it" }
                    .toImmutableList(),
                isChangelogDialogVisible = false,
                privacy = MR.files.dataprivacy.readToString(nativeApplication),
                isPrivacyDialogVisible = false,
                libraries = Libs.Builder().withJson(
                    MR.files.aboutlibraries.readToString(nativeApplication)
                ).build().libraries.map {
                    it.stable
                }.toImmutableList(),
                isLibraryDialogVisible = false,
                libraryDialogContent = null,
                snackBarText = null
            )
        )

    }

}