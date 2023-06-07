package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable

@Stable
abstract class IConfigurationEditViewState {
    abstract val isTestingEnabled: Boolean
}