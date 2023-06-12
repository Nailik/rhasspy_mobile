package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

@Stable
abstract class IConfigurationEditViewModel(
    private val testPageDestination: NavigationDestination,
) : KViewModel() {

    abstract val configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>

    fun onAction(action: IConfigurationEditUiEvent) {
        when (action) {
            Discard -> discard(false)
            Save -> save(false)
            OpenTestScreen -> navigator.navigate(testPageDestination)
            BackClick -> onBackClick()
            SaveDialog -> save(true)
            DiscardDialog -> discard(true)
            DismissDialog -> configurationEditViewState.update { it.copy(isShowUnsavedChangesDialog = false) }
            CloseServiceStateDialog -> configurationEditViewState.update { it.copy(isShowServiceStateDialog = false) }
            OpenServiceStateDialog -> configurationEditViewState.update { it.copy(isShowServiceStateDialog = true) }
        }
    }

    private fun save(popBackStack: Boolean) = updateData(popBackStack, ::onSave)

    private fun discard(popBackStack: Boolean) = updateData(popBackStack, ::onDiscard)

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            function()
            configurationEditViewState.update {
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
        if (configurationEditViewState.value.hasUnsavedChanges) {
            configurationEditViewState.update { it.copy(isShowUnsavedChangesDialog = true) }
        } else if (configurationEditViewState.value.isShowServiceStateDialog) {
            configurationEditViewState.update { it.copy(isShowServiceStateDialog = false) }
        } else if (!configurationEditViewState.value.isShowUnsavedChangesDialog) {
            onBackPressed()
        }
    }

    override fun onBackPressed(): Boolean {
        navigator.popBackStack()
        return true
    }

}