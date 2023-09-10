package org.rhasspy.mobile.viewmodel.screens.main

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
data class MainScreenViewState(
    val isShowCrashlyticsDialog: Boolean,
    val changelog: ImmutableList<String>,
    val isChangelogDialogVisible: Boolean
)