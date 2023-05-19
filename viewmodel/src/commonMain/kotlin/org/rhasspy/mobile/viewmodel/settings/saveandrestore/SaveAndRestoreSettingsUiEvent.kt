package org.rhasspy.mobile.viewmodel.settings.saveandrestore

sealed interface SaveAndRestoreSettingsUiEvent {

    sealed interface Action : SaveAndRestoreSettingsUiEvent {

        object ExportSettingsFile : Action
        object ExportSettingsFileConfirmation : Action
        object ExportSettingsFileDismiss : Action
        object RestoreSettingsFromFile : Action
        object RestoreSettingsFromFileConfirmation : Action
        object RestoreSettingsFromFileDismiss : Action
        object ShareSettingsFile : Action
        object BackClick : Action

    }

    sealed interface Consumed : SaveAndRestoreSettingsUiEvent {

        object ShowSnackBar : Consumed

    }


}