package org.rhasspy.mobile.viewmodel.screens.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination

class MainScreenViewStateCreator(
    private val navigator: Navigator
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    private val mainScreens = arrayOf(HomeScreen, ConfigurationScreen, SettingsScreen, LogScreen)

    operator fun invoke(): StateFlow<MainScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                navigator.backStack,
                navigator.getBackStack(MainNavigationDestination::class, HomeScreen).top,
                navigator.getBackStack(ConfigurationScreenDestination::class, ConfigurationScreenDestination.OverviewScreen).top,
                navigator.getBackStack(SettingsScreenDestination::class, SettingsScreenDestination.OverviewScreen).top,
                AppSetting.isShowLogEnabled.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): MainScreenViewState {
        val currentScreen = navigator.backStack.value.lastOrNull()?.top?.value
        val mainScreenIndex = mainScreens.indexOf(navigator.getBackStack(MainNavigationDestination::class, HomeScreen).top.value)

        return MainScreenViewState(
            isBottomNavigationVisible = currentScreen == HomeScreen ||
                    currentScreen == LogScreen ||
                    currentScreen == ConfigurationScreenDestination.OverviewScreen ||
                    currentScreen == SettingsScreenDestination.OverviewScreen,
            bottomNavigationIndex = mainScreenIndex,
            isShowLogEnabled = AppSetting.isShowLogEnabled.value
        )
    }
}