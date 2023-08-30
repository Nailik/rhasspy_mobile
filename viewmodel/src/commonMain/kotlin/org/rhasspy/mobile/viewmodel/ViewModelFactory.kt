package org.rhasspy.mobile.viewmodel

import androidx.compose.runtime.Stable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class ViewModelFactory : KoinComponent {

    inline fun <reified T : ScreenViewModel> getViewModel(): T {
        return get()
    }

    inline fun <reified T : ScreenViewModel> getViewModel(vararg parameters: Any?): T {
        return get { parametersOf(*parameters) }
    }

}