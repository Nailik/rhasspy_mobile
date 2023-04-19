package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.openLink
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode

/**
 * For About screen that displays app information
 * Holds changelog text and action to open source code link
 */
@Stable
class AboutScreenViewModel(
    viewStateCreator: AboutScreenViewStateCreator
) : ViewModel(), KoinComponent {

    val viewState: StateFlow<AboutScreenViewState> = viewStateCreator()

    fun onEvent(event: AboutScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenSourceCode -> openLink("https://github.com/Nailik/rhasspy_mobile")
        }
    }

}