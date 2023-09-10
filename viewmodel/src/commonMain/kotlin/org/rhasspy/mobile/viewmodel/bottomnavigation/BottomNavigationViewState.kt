package org.rhasspy.mobile.viewmodel.bottomnavigation

import androidx.compose.runtime.Stable

@Stable
data class BottomNavigationViewState(
    val bottomNavigationIndex: Int,
    val isShowLogEnabled: Boolean
)