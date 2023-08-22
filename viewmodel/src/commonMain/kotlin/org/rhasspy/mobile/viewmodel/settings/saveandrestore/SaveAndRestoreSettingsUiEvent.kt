package org.rhasspy.mobile.viewmodel.settings.saveandrestore

sealed interface SaveAndRestoreSettingsUiEvent {

    sealed interface Action : SaveAndRestoreSettingsUiEvent {

        data object ExportSettingsFile : Action
        data class ExportSettingsFileDialogResult(val confirmed: Boolean) : Action
        data object RestoreSettingsFromFile : Action
        data class RestoreSettingsFromFileDialogResult(val confirmed: Boolean) : Action
        data object ShareSettingsFile : Action
        data object BackClick : Action
        data object CloseRestoreSettingsFromDeprecatedFileDialog : Action

    }

    sealed interface Consumed : SaveAndRestoreSettingsUiEvent {

        object ShowSnackBar : Consumed

    }


}