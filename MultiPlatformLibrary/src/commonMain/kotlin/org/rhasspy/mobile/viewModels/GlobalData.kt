package org.rhasspy.mobile.viewModels

import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.MutableLiveData

object GlobalData {

    val settings: Settings = Settings()

    val unsavedChanges = MutableLiveData(false)

}