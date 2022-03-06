package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel

class ConfigurationScreenViewModel  : ViewModel() {

    var siteId = MutableLiveData("")
    var isHttpSSL = MutableLiveData(false)

}