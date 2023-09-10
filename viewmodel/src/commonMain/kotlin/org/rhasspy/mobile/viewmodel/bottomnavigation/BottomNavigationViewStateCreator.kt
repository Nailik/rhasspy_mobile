package org.rhasspy.mobile.viewmodel.bottomnavigation

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

class BottomNavigationViewStateCreator(
    private val navigator: INavigator
) {

    operator fun invoke(): StateFlow<BottomNavigationViewState> {

        return combineStateFlow(
            navigator.topScreen,
            AppSetting.isShowLogEnabled.data,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): BottomNavigationViewState {
        return BottomNavigationViewState(
            bottomNavigationIndex = when (navigator.topScreen.value as? NavigationDestination.MainScreenNavigationDestination?) {
                NavigationDestination.MainScreenNavigationDestination.HomeScreen          -> 0
                NavigationDestination.MainScreenNavigationDestination.DialogScreen        -> 1
                NavigationDestination.MainScreenNavigationDestination.ConfigurationScreen -> 2
                NavigationDestination.MainScreenNavigationDestination.SettingsScreen      -> 3
                NavigationDestination.MainScreenNavigationDestination.LogScreen           -> 4
                null                                                                      -> 1
            },
            isShowLogEnabled = AppSetting.isShowLogEnabled.value,
        )
    }

}