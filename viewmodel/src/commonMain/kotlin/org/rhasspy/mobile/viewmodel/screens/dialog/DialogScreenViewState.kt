package org.rhasspy.mobile.viewmodel.screens.dialog

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.pipeline.DomainResult

data class DialogScreenViewState(
    val isDialogAutoscroll: Boolean,
    val history: StateFlow<ImmutableList<DomainResult>>,
)