package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel

class MainViewModel : ViewModel() {

    fun saveAndApplyChanges() {
        GlobalData.saveAllChanges()
    }

    fun resetChanges() {
        GlobalData.resetChanges()
    }

}