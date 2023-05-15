package org.rhasspy.mobile.viewmodel.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.Screen.*
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.*

class MainScreenViewModel(
    viewStateCreator: MainScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: MainScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
            when (action) {
                BackClick -> navigator.popBackStack()
                BottomBarConfigurationClick -> navigator.set(ConfigurationScreen.OverviewScreen)
                BottomBarHomeClick -> navigator.set(HomeScreen)
                BottomBarLogClick -> navigator.set(LogScreen)
                BottomBarSettingsClick -> navigator.set(SettingsScreen.OverviewScreen)
            }
        }

}