package org.rhasspy.mobile.viewmodel.screens.main

import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.CloseChangelog
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.CrashlyticsDialogResult
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenUiEvent.Action.Navigate

class MainScreenViewModel(
    viewStateCreator: MainScreenViewStateCreator,
) : ScreenViewModel() {

    val viewState = viewStateCreator()
    val screen = navigator.topScreen

    fun onEvent(event: MainScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            is Navigate -> navigator.replace(
                MainScreenNavigationDestination::class,
                action.destination
            )

            is CrashlyticsDialogResult -> {
                AppSetting.isCrashlyticsEnabled.value = action.result
                AppSetting.didShowCrashlyticsDialog.value = true
            }

            CloseChangelog -> AppSetting.didShowChangelogDialog.value = BuildKonfig.versionCode
        }
    }

}