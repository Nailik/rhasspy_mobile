package org.rhasspy.mobile.viewmodel.settings.saveandrestore

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.platformspecific.settings.SettingsUtils
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent.Action.*

@Stable
class SaveAndRestoreSettingsViewModel : ViewModel() {

    private val logger = Logger.withTag("SaveAndRestoreSettingsViewModel")

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
                    logger.e(exception) { "restoreSettingsFromFile" }
                }
            }

            ShareSettingsFile -> SettingsUtils.shareSettingsFile()
        }
    }

}