package org.rhasspy.mobile.viewmodel.settings.saveandrestore

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.settings.SettingsUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed.ShowSnackBar

@Stable
class SaveAndRestoreSettingsViewModel : KViewModel() {

    private val _viewState = MutableStateFlow(SaveAndRestoreSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: SaveAndRestoreSettingsUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onAction(action: Action) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                ExportSettingsFile -> _viewState.update {
                    it.copy(isSaveSettingsToFileDialogVisible = true)
                }

                ExportSettingsFileDismiss -> _viewState.update {
                    it.copy(isSaveSettingsToFileDialogVisible = false)
                }

                RestoreSettingsFromFile -> _viewState.update {
                    it.copy(isRestoreSettingsFromFileDialogVisible = true)
                }

                RestoreSettingsFromFileDismiss -> _viewState.update {
                    it.copy(isRestoreSettingsFromFileDialogVisible = false)
                }

                ShareSettingsFile -> if (!SettingsUtils.shareSettingsFile()) {
                    _viewState.update {
                        it.copy(snackBarText = MR.strings.shareSettingsFileFailed.stable)
                    }
                }

                is BackClick -> navigator.onBackPressed()
                ExportSettingsFileConfirmation -> {
                    if (!SettingsUtils.exportSettingsFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.exportSettingsFileFailed.stable)
                        }
                    }
                }

                RestoreSettingsFromFileConfirmation -> {
                    if (!SettingsUtils.restoreSettingsFromFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.restoreSettingsFromFileFailed.stable)
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