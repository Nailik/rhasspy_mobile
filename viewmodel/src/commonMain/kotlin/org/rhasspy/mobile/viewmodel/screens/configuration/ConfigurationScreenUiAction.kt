package org.rhasspy.mobile.viewmodel.screens.configuration

sealed interface ConfigurationScreenUiAction {

    class SiteIdChange(val text: String): ConfigurationScreenUiAction
    object ScrollToError: ConfigurationScreenUiAction

}