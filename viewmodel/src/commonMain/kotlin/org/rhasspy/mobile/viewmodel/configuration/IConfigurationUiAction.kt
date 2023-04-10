package org.rhasspy.mobile.viewmodel.configuration


sealed interface IConfigurationUiAction {


    sealed interface IConfigurationEditUiAction : IConfigurationUiAction {

        object StartTest : IConfigurationEditUiAction
        object StopTest : IConfigurationEditUiAction
        object Save : IConfigurationEditUiAction
        object Discard : IConfigurationEditUiAction

    }

    sealed interface IConfigurationTestUiAction : IConfigurationUiAction {

        object ToggleListFiltered : IConfigurationTestUiAction
        object ToggleListAutoscroll : IConfigurationTestUiAction

    }
}