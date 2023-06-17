package org.rhasspy.mobile.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface IKViewModel {

    val screenViewState: StateFlow<ScreenViewState>

    fun onComposed()
    fun onDisposed()
    fun onEvent(event: KViewModelUiEvent)

}