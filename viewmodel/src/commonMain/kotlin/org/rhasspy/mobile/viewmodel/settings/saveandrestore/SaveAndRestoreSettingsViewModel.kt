package org.rhasspy.mobile.viewmodel.settings.saveandrestore

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.platformspecific.settings.SettingsUtils
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*

class SaveAndRestoreSettingsViewModel : ViewModel() {

    fun onEvent(event: SaveAndRestoreSettingsUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ExportSettingsFile -> SettingsUtils.exportSettingsFile()
            RestoreSettingsFromFile -> {
                try {
                    SettingsUtils.restoreSettingsFromFile()
                } catch (exception: Throwable) {
                    Logger.withTag("SaveAndRestoreSettingsViewModel").e(exception) { "restoreSettingsFromFile" }
                }
            }

            ShareSettingsFile -> SettingsUtils.shareSettingsFile()
        }
    }

}