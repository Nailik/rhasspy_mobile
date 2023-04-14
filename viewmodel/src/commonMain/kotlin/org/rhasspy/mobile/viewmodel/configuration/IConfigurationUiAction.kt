package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable

@Stable
sealed interface IConfigurationUiAction {

    @Stable
    sealed interface IConfigurationEditUiAction : IConfigurationUiAction {

        @Stable
        object StartTest : IConfigurationEditUiAction
        object StopTest : IConfigurationEditUiAction
        object Save : IConfigurationEditUiAction
        object Discard : IConfigurationEditUiAction
        object BackPress : IConfigurationEditUiAction
        object DismissDialog : IConfigurationEditUiAction

    }

    @Stable
    sealed interface IConfigurationTestUiAction : IConfigurationUiAction {

        object ToggleListFiltered : IConfigurationTestUiAction
        object ToggleListAutoscroll : IConfigurationTestUiAction

    }

}