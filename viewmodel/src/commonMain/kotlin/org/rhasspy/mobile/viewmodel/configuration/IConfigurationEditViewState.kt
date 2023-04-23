package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable

@Stable
abstract class IConfigurationEditViewState {
    abstract val isTestingEnabled: Boolean
}