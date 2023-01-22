package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.nativeutils.openLink

/**
 * For About screen that displays app information
 * Holds changelog text and action to open source code link
 */
class AboutScreenViewModel : ViewModel() {

    val changelogText = BuildKonfig.changelog

    fun onOpenSourceCode() {
        openLink("https://github.com/Nailik/rhasspy_mobile")
    }

}