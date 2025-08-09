package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.libraries.StableLibrary
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
data class AboutScreenViewState(
    val changelog: ImmutableList<String>,
    val isChangelogDialogVisible: Boolean,
    val privacy: String,
    val isPrivacyDialogVisible: Boolean,
    val libraries: ImmutableList<StableLibrary>,
    val isLibraryDialogVisible: Boolean,
    val libraryDialogContent: StableLibrary?,
    val snackBarText: StableStringResource?
)