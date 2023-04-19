package org.rhasspy.mobile.data.libraries

import androidx.compose.runtime.Stable
import com.mikepenz.aboutlibraries.entity.Library

val Library.stable get() = StableLibrary(this)

@Stable
data class StableLibrary(
    val library: Library
)