package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.openLink

/**
 * For About screen that displays app information
 * Holds changelog text and action to open source code link
 */
class AboutScreenViewModel : ViewModel(), KoinComponent {

    fun onOpenSourceCode() {
        openLink("https://github.com/Nailik/rhasspy_mobile")
    }

}