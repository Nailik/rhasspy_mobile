package org.rhasspy.mobile.viewmodel.screens.main

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.resource.readToString
import org.rhasspy.mobile.platformspecific.utils.isDebug
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.navigation.topScreen

class MainScreenViewStateCreator(
    private val navigator: INavigator,
    private val nativeApplication: NativeApplication,
) {

    operator fun invoke(): StateFlow<MainScreenViewState> {

        return combineStateFlow(
            navigator.navStack,
            AppSetting.isShowLogEnabled.data,
            AppSetting.didShowCrashlyticsDialog.data,
            AppSetting.didShowChangelogDialog.data,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MainScreenViewState {
        return MainScreenViewState(
            isBottomNavigationVisible = navigator.navStack.value.lastOrNull() is MainScreenNavigationDestination,
            bottomNavigationIndex = navigator.topScreen(HomeScreen).value.ordinal,
            isShowLogEnabled = AppSetting.isShowLogEnabled.value,
            isShowCrashlyticsDialog = !AppSetting.didShowCrashlyticsDialog.value && !isDebug(),
            changelog = Json.decodeFromString<JsonArray>(MR.files.changelog.readToString(nativeApplication))
                .map { "· ${it.jsonPrimitive.content}\n" }
                .toImmutableList(),
            isChangelogDialogVisible = AppSetting.didShowChangelogDialog.value < BuildKonfig.versionCode,
        )
    }
}