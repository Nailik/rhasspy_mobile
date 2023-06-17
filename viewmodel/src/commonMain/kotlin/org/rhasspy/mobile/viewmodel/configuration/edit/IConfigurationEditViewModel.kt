package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.Dialogs.UnsavedChangesDialog
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action
import org.rhasspy.mobile.viewmodel.ScreenViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Dialog.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Dialog
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

@Stable
abstract class IConfigurationEditViewModel<T>(
    private val testPageDestination: NavigationDestination
) : KViewModel() {

    protected abstract val _configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    val configurationEditViewState get() = _configurationEditViewState.readOnly

    protected abstract fun onDiscard()
    protected abstract fun onSave()

    fun onEvent(event: ConfigurationEditUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Dialog -> onDialog(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenTestScreen -> navigator.navigate(testPageDestination)
            BackClick -> onBackClick()
            OpenServiceStateDialog -> _screenViewState.update {
                it.copy(dialogViewState = serviceStateDialog(_configurationEditViewState.value.serviceViewState.serviceState.value))
            }

        }
    }

    private fun onDialog(dialogAction: Dialog) {
        val dialog = _configurationEditViewState.value.dialog

        _configurationEditViewState.update { it.copy(dialog = null) }

        when (dialog) {
            UnsavedChangesDialog ->
                when (dialogAction) {
                    Close -> discard(true)
                    Confirm -> save(true)
                    Dismiss -> Unit
                }

            else -> Unit
        }

    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            function()
            _screenViewState.update {
                it.copy(dialogViewState = null)
            }
            if (popBackStack) {
                navigator.popBackStack()
            }
        }
    }

    private fun onBackClick() {
        if (_screenViewState.value.dialogViewState != null) {
            if (_screenViewState.value.dialogViewState == unsavedChangesDialog()) {
                return
            } else {
                _screenViewState.update { it.copy(dialogViewState = null) }
            }
        } else if (_configurationEditViewState.value.hasUnsavedChanges) {
            _screenViewState.update { it.copy(dialogViewState = unsavedChangesDialog()) }
        } else {
            onBackPressed()
        }
    }

    override fun onBackPressed(): Boolean {
        navigator.popBackStack()
        return true
    }

    private fun unsavedChangesDialog() =
        ScreenViewState.DialogViewState(
            icon = Icons.Filled.Warning,
            title = MR.strings.unsavedChanges.stable,
            message = MR.strings.unsavedChangesInformation.stable,
            dismissButtonText = MR.strings.discard.stable,
            submitButtonText = MR.strings.save.stable
        )

    private fun serviceStateDialog(serviceState: ServiceState) =
        ScreenViewState.DialogViewState(
            icon = Icons.Filled.Info,
            title = MR.strings.error.stable,
            message = serviceState.getDialogText(),
            submitButtonText = MR.strings.close.stable
        )

}