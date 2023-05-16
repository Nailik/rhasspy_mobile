package org.rhasspy.mobile.viewmodel.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Navigate.*

class MainScreenViewModel(
    viewStateCreator: MainScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState = viewStateCreator()
    val screen = navigator.getBackStack(MainNavigationDestination::class, HomeScreen)

    fun onEvent(event: MainScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.popBackStack()
        }
    }

    private fun onNavigate(navigate: Navigate) {
        navigator.set(
            type = MainNavigationDestination::class,
            screen = when (navigate) {
                BottomBarConfigurationClick -> ConfigurationScreen
                BottomBarHomeClick -> HomeScreen
                BottomBarLogClick -> LogScreen
                BottomBarSettingsClick -> SettingsScreen
            }
        )
    }

}