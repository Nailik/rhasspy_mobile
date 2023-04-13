package org.rhasspy.mobile.viewmodel.configuration.webserver

sealed interface WebServerConfigurationUiAction {

    sealed interface Change: WebServerConfigurationUiAction {

    }

}