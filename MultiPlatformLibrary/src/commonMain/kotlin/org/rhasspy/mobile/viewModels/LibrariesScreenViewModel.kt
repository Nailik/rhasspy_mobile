package org.rhasspy.mobile.viewModels

import com.mikepenz.aboutlibraries.entity.Library
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.rhasspy.mobile.nativeutils.loadLibraries

class LibrariesScreenViewModel : ViewModel() {

    private val libraries = MutableLiveData<List<Library>>(listOf())
    val librariesUi = libraries.readOnly()

    init {
        viewModelScope.launch {
            libraries.postValue(loadLibraries())
        }
    }

}