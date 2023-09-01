package org.rhasspy.mobile.viewmodel.configuration.connections.http.list

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.repositories.IHttpConnectionSettingRepository
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination.HttpConnectionDetailScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class HttpConnectionListConfigurationViewModel(
    private val httpConnectionSettingRepository: IHttpConnectionSettingRepository
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(HttpConnectionListConfigurationViewState(persistentListOf()))
    val viewState = _viewState.readOnly

    init {
        collectHttpConnections()
    }

    private fun collectHttpConnections() {
        viewModelScope.launch(Dispatchers.IO) {
            httpConnectionSettingRepository.getAllHttpConnections().collect {
                _viewState.update { it.copy(items = it.items.toImmutableList()) }
            }
        }
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
        }
    }


}