package org.rhasspy.mobile.viewmodel

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Stable
class ViewModelFactory : KoinComponent {

    inline fun <reified T: ViewModel> getViewModel(): T {
        return get()
    }

}