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
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination

class MainScreenViewStateCreator(
    private val navigator: INavigator,
    private val nativeApplication: NativeApplication,
) {

    operator fun invoke(): StateFlow<MainScreenViewState> {

        return combineStateFlow(
            navigator.topScreen,
            AppSetting.isShowLogEnabled.data,
            AppSetting.didShowCrashlyticsDialog.data,
            AppSetting.didShowChangelogDialog.data
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MainScreenViewState {
        return MainScreenViewState(
            bottomNavigationIndex = (navigator.topScreen.value as? MainScreenNavigationDestination?)?.ordinal
                ?: 1,
            isShowLogEnabled = AppSetting.isShowLogEnabled.value,
            isShowCrashlyticsDialog = !AppSetting.didShowCrashlyticsDialog.value,
            changelog = Json.decodeFromString<JsonArray>(
                MR.files.changelog_json.readToString(
                    nativeApplication
                )
            )
                .map { "· ${it.jsonPrimitive.content}\n" }
                .toImmutableList(),
            isChangelogDialogVisible = AppSetting.didShowChangelogDialog.value < BuildKonfig.versionCode
        )
    }
}