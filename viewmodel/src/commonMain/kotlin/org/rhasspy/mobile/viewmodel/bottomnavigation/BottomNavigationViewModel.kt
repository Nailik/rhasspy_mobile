package org.rhasspy.mobile.viewmodel.bottomnavigation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.viewmodel.bottomnavigation.BottomNavigationUiEvent.Action
import org.rhasspy.mobile.viewmodel.bottomnavigation.BottomNavigationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination

class BottomNavigationViewModel(
    viewStateCreator: BottomNavigationViewStateCreator,
    private val navigator: INavigator
) : ViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: BottomNavigationUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is Navigate -> navigator.replace(MainScreenNavigationDestination::class, action.destination)
        }
    }

}