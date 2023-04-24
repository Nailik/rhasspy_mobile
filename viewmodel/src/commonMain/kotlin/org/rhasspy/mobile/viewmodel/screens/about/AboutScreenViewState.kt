package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.libraries.StableLibrary

@Stable
data class AboutScreenViewState internal constructor(
    val changelog: ImmutableList<String>,
    val privacy: String,
    val libraries: ImmutableList<StableLibrary>
)