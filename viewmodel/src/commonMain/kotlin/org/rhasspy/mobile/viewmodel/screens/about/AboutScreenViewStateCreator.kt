package org.rhasspy.mobile.viewmodel.screens.about

import com.mikepenz.aboutlibraries.Libs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.data.libraries.stable
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.resource.readToString
import org.rhasspy.mobile.resources.MR

class AboutScreenViewStateCreator(
    val nativeApplication: NativeApplication,
) {

    operator fun invoke(): MutableStateFlow<AboutScreenViewState> {
        return MutableStateFlow(
            AboutScreenViewState(
                changelog = Json.decodeFromString<JsonArray>(
                    MR.files.changelog_json.readToString(
                        nativeApplication
                    )
                )
                    .map { "· ${it.jsonPrimitive.content}\n" }
                    .toImmutableList(),
                isChangelogDialogVisible = false,
                privacy = MR.files.dataprivacy_html.readToString(nativeApplication),
                isPrivacyDialogVisible = false,
                libraries = Libs.Builder().withJson(
                    MR.files.aboutlibraries_json.readToString(nativeApplication)
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