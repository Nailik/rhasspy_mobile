package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.HttpConnectionDetailScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class HttpConnectionListConfigurationViewModel : ScreenViewModel() {

    private val _viewState = MutableStateFlow(HttpConnectionListConfigurationViewState())
    val viewState = _viewState.readOnly

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