package org.rhasspy.mobile.viewmodel.screens.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.Screen

class MainScreenViewStateCreator(
    private val navigator: Navigator
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    private val mainScreens = arrayOf(
        Screen.HomeScreen,
        Screen.ConfigurationScreen.OverviewScreen,
        Screen.SettingsScreen.OverviewScreen,
        Screen.LogScreen
    )

    operator fun invoke(): StateFlow<MainScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                navigator.backStack,
                AppSetting.isShowLogEnabled.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): MainScreenViewState {
        val backStack = navigator.backStack.value
        val currentScreen = backStack.last()
        val index = if (currentScreen in mainScreens) {
            mainScreens.indexOf(currentScreen)
        } else {
            mainScreens.indexOf(backStack.first())
        }

        return MainScreenViewState(
            screen = currentScreen,
            isBottomNavigationVisible = currentScreen in mainScreens,
            bottomNavigationIndex = index,
            isShowLogEnabled = AppSetting.isShowLogEnabled.value
        )
    }
}