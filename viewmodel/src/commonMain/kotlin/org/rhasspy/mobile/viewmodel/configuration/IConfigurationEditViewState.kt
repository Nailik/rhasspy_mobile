package org.rhasspy.mobile.viewmodel.configuration

interface IConfigurationEditViewState {
    val hasUnsavedChanges: Boolean
    val isTestingEnabled: Boolean
}