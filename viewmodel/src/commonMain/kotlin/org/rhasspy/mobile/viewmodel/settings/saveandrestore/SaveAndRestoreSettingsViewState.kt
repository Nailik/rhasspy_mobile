package org.rhasspy.mobile.viewmodel.settings.saveandrestore

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
data class SaveAndRestoreSettingsViewState(
    val snackBarText: StableStringResource? = null,
    val isSaveSettingsToFileDialogVisible: Boolean = false,
    val isRestoreSettingsFromFileDialogVisible: Boolean = false
)