package org.rhasspy.mobile.viewmodel.screens.main

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.navigation.topScreen

class MainScreenViewStateCreator(
    private val navigator: INavigator
) {

    operator fun invoke(): StateFlow<MainScreenViewState> {

        return combineStateFlow(
            navigator.navStack,
            AppSetting.isShowLogEnabled.data,
            AppSetting.didShowCrashlyticsDialog.data
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MainScreenViewState {
        return MainScreenViewState(
            isBottomNavigationVisible = navigator.navStack.value.lastOrNull() is MainScreenNavigationDestination,
            bottomNavigationIndex = navigator.topScreen(HomeScreen).value.ordinal,
            isShowLogEnabled = AppSetting.isShowLogEnabled.value,
            isShowCrashlyticsDialog = !AppSetting.didShowCrashlyticsDialog.value && !isDebug()
        )
    }
}