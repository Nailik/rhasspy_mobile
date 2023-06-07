package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Logger
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestViewState
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
abstract class IConfigurationEditViewModel<V : IConfigurationEditViewState>(
    private val service: IService,
    private val initialViewState: () -> V,
    private val testPageDestination: NavigationDestination
) : KViewModel() {

    private val logger = Logger.withTag("IConfigurationViewModel")

    protected val contentViewState = MutableStateFlow(initialViewState())
    protected val data get() = contentViewState.value

    private val _viewState = MutableStateFlow(
        ConfigurationViewState(
            serviceViewState = ServiceViewState(service.serviceState),
            isOpenServiceStateDialogEnabled = service.serviceState.value.isOpenServiceStateDialogEnabled(),
            isShowServiceStateDialog = false,
            serviceStateDialogText = service.serviceState.value.getDialogText(),
            editViewState = contentViewState,
            testViewState = IConfigurationTestViewState,
            isShowUnsavedChangesDialog = false,
            hasUnsavedChanges = false
        )
    )
    val viewState = _viewState.readOnly

    init {
        viewModelScope.launch(Dispatchers.IO) {
            service.serviceState.collect { serviceState ->
                _viewState.update {
                    it.copy(
                        isOpenServiceStateDialogEnabled = serviceState.isOpenServiceStateDialogEnabled(),
                        serviceStateDialogText = serviceState.getDialogText()
                    )
                }
            }
        }
    }

    private fun ServiceState.isOpenServiceStateDialogEnabled(): Boolean = (this is ServiceState.Exception || this is ServiceState.Error)
    private fun ServiceState.getDialogText(): Any = when (this) {
        is ServiceState.Error -> this.information
        is ServiceState.Exception -> this.exception?.toString() ?: ""
        else -> ""
    }

    protected fun updateViewState(function: (V) -> V) {
        val newContentViewState = function(contentViewState.value)
        _viewState.update {
            it.copy(hasUnsavedChanges = newContentViewState != initialViewState())
        }
        contentViewState.value = newContentViewState
    }

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

    private fun save(popBackStack: Boolean) {
        updateData(popBackStack, ::onSave)
    }

    private fun discard(popBackStack: Boolean) {
        updateData(popBackStack, ::onDiscard)
    }

    private fun updateData(popBackStack: Boolean, function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            function()
            contentViewState.value = initialViewState()
            noUnsavedChanges()
            if (popBackStack) {
                navigator.popBackStack()
            }
        }
    }

    private fun noUnsavedChanges() {
        _viewState.update {
            it.copy(
                isShowUnsavedChangesDialog = false,
                hasUnsavedChanges = false
            )
        }
    }

    protected abstract fun onDiscard()

    protected abstract fun onSave()

    private fun onBackClick() {
        if (_viewState.value.hasUnsavedChanges) {
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