package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.DialogState.ServiceStateDialogState
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.DialogAction
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.DialogAction.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class IConfigurationViewModel(
    private val service: IService
) : ScreenViewModel() {

    protected val viewStateCreator by inject<IConfigurationViewStateCreator> { parametersOf(service) }

    private val _configurationViewState = MutableStateFlow(ConfigurationViewState(serviceViewState = ServiceViewState(service.serviceState)))
    val configurationEditViewState by lazy { initViewStateCreator(_configurationViewState) }
    abstract fun initViewStateCreator(configurationViewState: MutableStateFlow<ConfigurationViewState>): StateFlow<ConfigurationViewState>

    protected abstract fun onDiscard()
    protected abstract fun onSave()


    fun onEvent(event: IConfigurationUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is DialogAction -> onDialog(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenServiceStateDialog -> _configurationViewState.update { it.copy(dialogState = ServiceStateDialogState(service.serviceState.value)) }
            BackClick -> navigator.onBackPressed()
        }
    }

    private fun onDialog(dialogAction: DialogAction) {

        _configurationViewState.update { it.copy(dialogState = null) }

        when (dialogAction.dialogState) {
            UnsavedChangesDialogState ->
                when (dialogAction) {
                    is Close -> Unit
                    is Confirm -> save(true)
                    is Dismiss -> discard(true)
                }

            else -> Unit
        }

    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            function()
            _configurationViewState.update { it.copy(dialogState = null, hasUnsavedChanges = false) }
            if (popBackStack) {
                navigator.popBackStack()
            }
        }

    }

    override fun onBackPressed(): Boolean {
        return when (_configurationViewState.value.dialogState) {
            UnsavedChangesDialogState -> true

            null -> {
                if (_configurationViewState.value.hasUnsavedChanges) {
                    _configurationViewState.update { it.copy(dialogState = UnsavedChangesDialogState) }
                    true
                } else false
            }

            else -> {
                _configurationViewState.update { it.copy(dialogState = null) }
                true
            }
        }
    }

}