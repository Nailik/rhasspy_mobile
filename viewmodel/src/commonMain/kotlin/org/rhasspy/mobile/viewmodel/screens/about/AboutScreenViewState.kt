package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import com.mikepenz.aboutlibraries.entity.Library
import kotlinx.collections.immutable.ImmutableList

@Stable
data class AboutScreenViewState internal constructor(
    val changelog: ImmutableList<String>,
    val privacy: String,
    val libraries: ImmutableList<Library>
)