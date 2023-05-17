package org.rhasspy.mobile.viewmodel

import androidx.compose.runtime.Stable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Stable
class ViewModelFactory : KoinComponent {

    inline fun <reified T : KViewModel> getViewModel(): T {
        return get()
    }

}