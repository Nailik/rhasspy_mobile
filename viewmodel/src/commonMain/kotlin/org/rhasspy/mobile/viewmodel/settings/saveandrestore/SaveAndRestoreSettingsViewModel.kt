package org.rhasspy.mobile.viewmodel.settings.saveandrestore

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.settings.SettingsUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Consumed.ShowSnackBar

@Stable
class SaveAndRestoreSettingsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(SaveAndRestoreSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: SaveAndRestoreSettingsUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onAction(action: Action) {
        viewModelScope.launch(Dispatchers.Default) {
            when (action) {
                ExportSettingsFile -> {
                    if (!SettingsUtils.exportSettingsFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.exportSettingsFileFailed.stable)
                        }
                    }
                }

                RestoreSettingsFromFile -> {
                    if (!SettingsUtils.restoreSettingsFromFile()) {
                        _viewState.update {
                            it.copy(snackBarText = MR.strings.restoreSettingsFromFileFailed.stable)
                        }
                    }
                }

                ShareSettingsFile -> if (!SettingsUtils.shareSettingsFile()) {
                    _viewState.update {
                        it.copy(snackBarText = MR.strings.shareSettingsFileFailed.stable)
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

}