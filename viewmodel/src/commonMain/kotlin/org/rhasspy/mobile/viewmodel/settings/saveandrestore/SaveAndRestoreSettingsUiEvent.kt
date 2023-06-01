package org.rhasspy.mobile.viewmodel.settings.saveandrestore

sealed interface SaveAndRestoreSettingsUiEvent {

    sealed interface Action : SaveAndRestoreSettingsUiEvent {

        object ExportSettingsFile : Action
        data class ExportSettingsFileDialogResult(val confirmed: Boolean) : Action
        object RestoreSettingsFromFile : Action
        data class RestoreSettingsFromFileDialogResult(val confirmed: Boolean) : Action
        object ShareSettingsFile : Action
        object BackClick : Action

    }

    sealed interface Consumed : SaveAndRestoreSettingsUiEvent {

        object ShowSnackBar : Consumed

    }


}