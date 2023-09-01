package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import org.rhasspy.mobile.data.connection.HttpConnection

sealed interface HttpConnectionListConfigurationUiEvent {

    sealed interface Action : HttpConnectionListConfigurationUiEvent {

        data object BackClick : Action
        data object AddClick : Action
        data class ItemClick(val id: HttpConnection) : Action

    }

}