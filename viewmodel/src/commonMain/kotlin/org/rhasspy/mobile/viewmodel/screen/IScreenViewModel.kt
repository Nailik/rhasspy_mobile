package org.rhasspy.mobile.viewmodel.screen

import kotlinx.coroutines.flow.StateFlow

interface IScreenViewModel {

    val screenViewState: StateFlow<ScreenViewState>

    fun onComposed()
    fun onDisposed()
    fun onEvent(event: ScreenViewModelUiEvent)
    fun onBackPressedClick(): Boolean

}