package org.rhasspy.mobile.viewmodel.screens.dialog

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.logic.pipeline.DomainResult

data class DialogScreenViewState(
    val isDialogAutoscroll: Boolean,
    val history: ImmutableList<DomainResult>,
)