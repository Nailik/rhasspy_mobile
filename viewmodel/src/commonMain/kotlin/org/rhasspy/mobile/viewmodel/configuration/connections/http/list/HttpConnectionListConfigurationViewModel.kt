package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationViewState.HttpConfigurationItemViewState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.HttpConnectionDetailScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class HttpConnectionListConfigurationViewModel : ScreenViewModel() {

    val viewState = ConfigurationSetting.httpConnections.data.mapReadonlyState { list ->
        HttpConnectionListConfigurationViewState(list.map { HttpConfigurationItemViewState(it) }.toImmutableList())
    }

    fun onEvent(event: HttpConnectionListConfigurationUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick    -> navigator.onBackPressed()
            AddClick     -> navigator.navigate(HttpConnectionDetailScreen(null))
            is ItemClick -> navigator.navigate(HttpConnectionDetailScreen(action.id))
            is ItemDeleteClick -> TODO()
        }
    }


}