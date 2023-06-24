package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.DialogAction
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.DialogAction.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState.ServiceStateDialogState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class IConfigurationEditViewModel(
    private val service: IService
) : ScreenViewModel() {

    private val _configurationEditViewState = MutableStateFlow(ConfigurationEditViewState(serviceViewState = ServiceViewState(service.serviceState)))
    val configurationEditViewState by lazy { initViewStateCreator(_configurationEditViewState) }

    abstract fun initViewStateCreator(configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>): StateFlow<ConfigurationEditViewState>

    protected abstract fun onDiscard()
    protected abstract fun onSave()

    fun onEvent(event: ConfigurationEditUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is DialogAction -> onDialog(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenServiceStateDialog -> _configurationEditViewState.update { it.copy(dialogState = ServiceStateDialogState(service.serviceState.value)) }
            BackClick -> onBackPressedClick()
        }
    }

    private fun onDialog(dialogAction: DialogAction) {

        _configurationEditViewState.update { it.copy(dialogState = null) }

        when (dialogAction.dialogState) {
            UnsavedChangesDialogState ->
                when (dialogAction) {
                    is Close -> discard(true)
                    is Confirm -> save(true)
                    is Dismiss -> Unit
                }

            else -> Unit
        }

    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            function()
            _configurationEditViewState.update { it.copy(dialogState = null) }
            if (popBackStack) {
                navigator.popBackStack()
            }
        }

    }

    override fun onBackPressed(): Boolean {
        return when (_configurationEditViewState.value.dialogState) {
            null -> false
            UnsavedChangesDialogState -> true
            else -> {
                _configurationEditViewState.update { it.copy(dialogState = null) }
                true
            }
        }
    }

}