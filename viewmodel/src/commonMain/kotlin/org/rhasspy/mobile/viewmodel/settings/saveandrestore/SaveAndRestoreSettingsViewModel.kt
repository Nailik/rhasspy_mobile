package org.rhasspy.mobile.viewmodel.settings.saveandrestore

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.settings.ISettingsUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed.ShowSnackBar

@Stable
class SaveAndRestoreSettingsViewModel(
    private val settingsUtils: ISettingsUtils
) : ScreenViewModel() {

    private val dispatcher by inject<IDispatcherProvider>()

    private val _viewState = MutableStateFlow(SaveAndRestoreSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: SaveAndRestoreSettingsUiEvent) {
        when (event) {
            is Action   -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onAction(action: Action) {
        viewModelScope.launch(dispatcher.IO) {
            _viewState.update {
                when (action) {
                    ExportSettingsFile                           ->
                        it.copy(isSaveSettingsToFileDialogVisible = true)

                    RestoreSettingsFromFile                      ->
                        it.copy(isRestoreSettingsFromFileDialogVisible = true)

                    ShareSettingsFile                            ->
                        if (!settingsUtils.shareSettingsFile()) {
                            it.copy(snackBarText = MR.strings.shareSettingsFileFailed.stable)
                        } else it

                    is BackClick                                 -> {
                        navigator.onBackPressed()
                        it
                    }

                    is ExportSettingsFileDialogResult            -> {
                        if (action.confirmed) {
                            if (!settingsUtils.exportSettingsFile()) {
                                it.copy(
                                    snackBarText = MR.strings.exportSettingsFileFailed.stable,
                                    isSaveSettingsToFileDialogVisible = false
                                )
                            } else it.copy(isSaveSettingsToFileDialogVisible = false)
                        } else {
                            it.copy(isSaveSettingsToFileDialogVisible = false)
                        }
                    }

                    is RestoreSettingsFromFileDialogResult       -> {
                        if (action.confirmed) {
                            if (!settingsUtils.restoreSettingsFromFile())
                                it.copy(
                                    snackBarText = MR.strings.restoreSettingsFromFileFailed.stable,
                                    isRestoreSettingsFromFileDialogVisible = false
                                ) else it.copy(isRestoreSettingsFromFileDialogVisible = false)
                        } else {
                            it.copy(isRestoreSettingsFromFileDialogVisible = false)
                        }
                    }
                }
            }
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return if (_viewState.value.isRestoreSettingsFromFileDialogVisible) {
            _viewState.update { it.copy(isRestoreSettingsFromFileDialogVisible = false) }
            true
        } else if (_viewState.value.isSaveSettingsToFileDialogVisible) {
            _viewState.update { it.copy(isSaveSettingsToFileDialogVisible = false) }
            true
        } else {
            false
        }
    }

}