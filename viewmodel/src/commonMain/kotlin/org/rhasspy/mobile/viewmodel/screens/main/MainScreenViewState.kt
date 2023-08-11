package org.rhasspy.mobile.viewmodel.screens.main

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
data class MainScreenViewState(
    val bottomNavigationIndex: Int,
    val isShowLogEnabled: Boolean,
    val isShowCrashlyticsDialog: Boolean,
    val changelog: ImmutableList<String>,
    val isChangelogDialogVisible: Boolean
)