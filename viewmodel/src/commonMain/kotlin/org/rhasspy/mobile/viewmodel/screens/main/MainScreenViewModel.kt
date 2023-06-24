package org.rhasspy.mobile.viewmodel.screens.main

import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.*

class MainScreenViewModel(
    viewStateCreator: MainScreenViewStateCreator
) : ScreenViewModel() {

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
            is CrashlyticsDialogResult -> {
                AppSetting.isCrashlyticsEnabled.value = action.result
                AppSetting.didShowCrashlyticsDialog.value = true
            }
        }
    }

}