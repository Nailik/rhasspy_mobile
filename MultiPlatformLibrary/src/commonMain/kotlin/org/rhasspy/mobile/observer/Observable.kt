package org.rhasspy.mobile.observer

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class Observable<T>(initialValue: T) {

    private val viewScope = CoroutineScope(Dispatchers.Main)

    // List ov observers watching this value for changes
    protected val observers = mutableListOf<(T) -> Unit>()

    // The real value of this observer
    // Doesn't need a custom getter, but the setter
    // we override to allow notifying all observers
    open val value: T = initialValue

    //create only one live data, else there might be loads of dead observers
    private val liveData = MutableLiveData(initialValue).also { live ->
        observers.add {
            viewScope.launch {
                live.value = it
            }
        }
    }

    fun observe(observer: (T) -> Unit) {
        observers.add(observer)
        observer.invoke(value)
    }

    fun toLiveData(): LiveData<T> = liveData.readOnly()
}
