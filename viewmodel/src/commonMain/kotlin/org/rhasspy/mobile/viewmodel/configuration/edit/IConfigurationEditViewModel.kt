package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.Dialog.UnsavedChangesDialog
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.DialogAction.*
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditUiEvent.DialogAction
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.Dialog.ServiceStateDialog
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
            is DialogAction -> onDialog(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenTestScreen -> navigator.navigate(testPageDestination)
            BackClick -> onBackClick()
            OpenServiceStateDialog -> _configurationEditViewState.update {
                it.copy(dialog = ServiceStateDialog(_configurationEditViewState.value.serviceViewState.serviceState.value))
            }
        }
    }

    private fun onDialog(dialogAction: DialogAction) {

        when (dialogAction.dialog) {
            UnsavedChangesDialog ->
                when (dialogAction) {
                    is Close -> discard(true)
                    is Confirm -> save(true)
                    is Dismiss -> Unit
                }

            else -> Unit
        }

        _configurationEditViewState.update { it.copy(dialog = null) }

    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            function()
            _configurationEditViewState.update {
                it.copy(dialog = null)
            }
            if (popBackStack) {
                navigator.popBackStack()
            }
        }

    }

    private fun onBackClick() {

        when (_configurationEditViewState.value.dialog) {
            is ServiceStateDialog ->
                _configurationEditViewState.update {
                    it.copy(dialog = null)
                }

            UnsavedChangesDialog -> return
            null -> onBackPressed()
        }

    }

    override fun onBackPressed(): Boolean {
        navigator.popBackStack()
        return true
    }

}