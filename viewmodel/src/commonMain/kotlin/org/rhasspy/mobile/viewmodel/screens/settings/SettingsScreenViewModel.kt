package org.rhasspy.mobile.viewmodel.screens.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.platformspecific.readOnly

class SettingsScreenViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(SettingsScreenViewState())
    val viewState = _viewState.readOnly

    init {
        SettingsScreenViewStateUpdater(_viewState)
    }

}