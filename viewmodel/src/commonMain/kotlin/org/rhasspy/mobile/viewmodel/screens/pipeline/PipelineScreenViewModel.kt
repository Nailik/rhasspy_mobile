package org.rhasspy.mobile.viewmodel.screens.pipeline

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewState

class PipelineScreenViewModel {

    private val _viewState = MutableStateFlow(PipelineScreenViewState())
    val viewState = _viewState.readOnly

}