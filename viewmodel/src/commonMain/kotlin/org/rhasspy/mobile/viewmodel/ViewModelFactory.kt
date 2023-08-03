package org.rhasspy.mobile.viewmodel

import androidx.compose.runtime.Stable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class ViewModelFactory : KoinComponent {

    inline fun <reified T : ScreenViewModel> getViewModel(): T {
        return get()
    }

}