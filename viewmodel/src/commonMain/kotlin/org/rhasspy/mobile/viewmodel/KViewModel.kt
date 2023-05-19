package org.rhasspy.mobile.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.viewmodel.navigation.Navigator

abstract class KViewModel : ViewModel(), KoinComponent {

    protected val navigator by inject<Navigator>()

    fun composed() {
        navigator.onComposed(this)
    }

    fun disposed() {
        navigator.onDisposed(this)
    }

    /**
     * returns true if pop back stack was handled internally
     */
    open fun onBackPressed(): Boolean {
        return false
    }

}