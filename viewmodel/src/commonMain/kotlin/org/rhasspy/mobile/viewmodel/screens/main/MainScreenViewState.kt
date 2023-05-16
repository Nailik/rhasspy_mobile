package org.rhasspy.mobile.viewmodel.screens.main

import androidx.compose.runtime.Stable

@Stable
data class MainScreenViewState(
    val isBottomNavigationVisible: Boolean,
    val bottomNavigationIndex: Int,
    val isShowLogEnabled: Boolean
)