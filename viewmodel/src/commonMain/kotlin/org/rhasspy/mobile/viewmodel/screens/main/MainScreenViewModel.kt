package org.rhasspy.mobile.viewmodel.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.Navigate

class MainScreenViewModel(
    viewStateCreator: MainScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState = viewStateCreator()
    val screen = navigator.topScreen<MainScreenNavigationDestination>()

    fun onEvent(event: MainScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.popBackStack()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

}