package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow

@Stable
class SettingsScreenViewModel(
    viewStateCreator: SettingsScreenViewStateCreator
) : ViewModel() {

    val viewState: StateFlow<SettingsScreenViewState> = viewStateCreator()

}