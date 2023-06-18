package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Change.OpenServiceStateDialog
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.DialogAction.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState.ServiceStateDialogState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.DialogState.UnsavedChangesDialogState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class IConfigurationEditViewModel(
    private val testPageDestination: NavigationDestination,
    service: IService,
) : KViewModel() {

    private val _configurationEditViewState = MutableStateFlow(ConfigurationEditViewState(serviceViewState = ServiceViewState(service.serviceState)))
    val configurationEditViewState by lazy { initViewStateCreator(_configurationEditViewState) }

    abstract fun initViewStateCreator(configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>) : StateFlow<ConfigurationEditViewState>

    protected abstract fun onDiscard()
    protected abstract fun onSave()

    fun onEvent(event: ConfigurationEditUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is DialogAction -> onDialog(event)
            is Change -> onChange(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenTestScreen -> navigator.navigate(testPageDestination)
            BackClick -> onBackClick()
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

    private fun onChange(change: Change) {
        _configurationEditViewState.update {
            when (change) {
                OpenServiceStateDialog -> it.copy(dialogState = ServiceStateDialogState(it.serviceViewState.serviceState.value))
            }
        }
    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            function()
            _configurationEditViewState.update {
                it.copy(dialogState = null)
            }
            if (popBackStack) {
                navigator.popBackStack()
            }
        }

    }

    private fun onBackClick() {

        when (_configurationEditViewState.value.dialogState) {
            is ServiceStateDialogState ->
                _configurationEditViewState.update {
                    it.copy(dialogState = null)
                }

            UnsavedChangesDialogState -> return
            null -> onBackPressed()
        }

    }

    override fun onBackPressed(): Boolean {
        navigator.popBackStack()
        return true
    }

}