package org.rhasspy.mobile.viewmodel.screens.main

import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.Navigate

class MainScreenViewModel(
    viewStateCreator: MainScreenViewStateCreator
) : KViewModel() {

    val viewState = viewStateCreator()
    val screen = navigator.topScreen(HomeScreen)

    fun onEvent(event: MainScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            is Navigate -> navigator.replace<MainScreenNavigationDestination>(action.destination)
        }
    }

}