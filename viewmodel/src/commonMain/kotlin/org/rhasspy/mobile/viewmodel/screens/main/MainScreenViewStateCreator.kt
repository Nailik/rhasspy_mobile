package org.rhasspy.mobile.viewmodel.screens.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination.*

class MainScreenViewStateCreator(
    private val navigator: Navigator
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    private val mainScreens = arrayOf(HomeScreen, ConfigurationScreen, SettingsScreen, LogScreen)

    operator fun invoke(): StateFlow<MainScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                navigator.getBackStack(MainNavigationDestination::class, HomeScreen).top,
                AppSetting.isShowLogEnabled.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): MainScreenViewState {
        val currentScreen = navigator.getBackStack(MainNavigationDestination::class, HomeScreen).top.value

        return MainScreenViewState(
            isBottomNavigationVisible = currentScreen in mainScreens,
            bottomNavigationIndex = mainScreens.indexOf(currentScreen),
            isShowLogEnabled = AppSetting.isShowLogEnabled.value
        )
    }
}