package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class IConfigurationEditViewModel <T: ConfigurationEditViewState<*>>(
    service: IService,
    viewStateCreator: ConfigurationDataStateCreator<T>,
    private val testPageDestination: NavigationDestination
) : KViewModel() {

    private val _viewState = MutableStateFlow(
        ConfigurationEditViewState(
            serviceViewState = ServiceViewState(service.serviceState),
            isShowServiceStateDialog = false,
            isShowUnsavedChangesDialog = false,
            dataState = viewStateCreator()
        )
    )
    val viewState = _viewState.readOnly

    fun onAction(action: IConfigurationEditUiEvent) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenTestScreen -> navigator.navigate(testPageDestination)
            BackClick -> onBackClick()
            SaveDialog -> save(true)
            DiscardDialog -> discard(true)
            DismissDialog -> _viewState.update { it.copy(isShowUnsavedChangesDialog = false) }
            CloseServiceStateDialog -> _viewState.update { it.copy(isShowServiceStateDialog = false) }
            OpenServiceStateDialog -> _viewState.update { it.copy(isShowServiceStateDialog = true) }
        }
    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            function()
            _viewState.update {
                it.copy(isShowUnsavedChangesDialog = false)
            }
            if (popBackStack) {
                navigator.popBackStack()
            }
        }
    }

    protected abstract fun onDiscard()

    protected abstract fun onSave()

    private fun onBackClick() {
        if (_viewState.value.dataState.value.hasUnsavedChanges) {
            _viewState.update { it.copy(isShowUnsavedChangesDialog = true) }
        } else if (_viewState.value.isShowServiceStateDialog) {
            _viewState.update { it.copy(isShowServiceStateDialog = false) }
        } else if (!_viewState.value.isShowUnsavedChangesDialog) {
            onBackPressed()
        }
    }

    override fun onBackPressed(): Boolean {
        navigator.popBackStack()
        return true
    }

}