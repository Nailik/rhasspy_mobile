package org.rhasspy.mobile.viewmodel.screens.main

import org.rhasspy.mobile.viewmodel.navigation.Screen

data class MainScreenViewState(
    val screen: Screen,
    val isBottomNavigationVisible: Boolean,
    val bottomNavigationIndex: Int,
    val isShowLogEnabled: Boolean
)