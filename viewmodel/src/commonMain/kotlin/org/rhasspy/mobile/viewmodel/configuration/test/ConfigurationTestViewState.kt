package org.rhasspy.mobile.viewmodel.configuration.test

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.log.LogElement

@Stable
abstract class ConfigurationTestViewState internal constructor(
    val isListFiltered: Boolean = false,
    val isListAutoscroll: Boolean = true,
    val logEvents: StateFlow<ImmutableList<LogElement>>
)