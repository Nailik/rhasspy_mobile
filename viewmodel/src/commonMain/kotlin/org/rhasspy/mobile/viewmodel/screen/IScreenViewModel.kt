package org.rhasspy.mobile.viewmodel.screen

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface IScreenViewModel {

    val screenViewState: StateFlow<ScreenViewState>

    fun onComposed()
    fun onDisposed()
    fun onEvent(event: ScreenViewModelUiEvent)
    fun onBackPressedClick(): Boolean

}