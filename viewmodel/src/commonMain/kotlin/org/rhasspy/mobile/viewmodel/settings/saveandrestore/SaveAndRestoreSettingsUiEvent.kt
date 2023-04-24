package org.rhasspy.mobile.viewmodel.settings.saveandrestore

sealed interface SaveAndRestoreSettingsUiEvent {

    sealed interface Action : SaveAndRestoreSettingsUiEvent {
        object ExportSettingsFile : Action
        object RestoreSettingsFromFile : Action
        object ShareSettingsFile : Action
    }
}