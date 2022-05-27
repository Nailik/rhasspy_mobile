package org.rhasspy.mobile.observer

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Observable<T>(initialValue: T) {

    private val viewScope = CoroutineScope(Dispatchers.Main)
    // List ov observers watching this value for changes
    private val observers = mutableListOf<(T) -> Unit>()

    // The real value of this observer
    // Doesn't need a custom getter, but the setter
    // we override to allow notifying all observers
    var value: T = initialValue
        set(value) {
            field = value
            //invoke all observers
            observers.forEach { observer ->
                observer.invoke(value)
            }
        }

    fun observe(observer: (T) -> Unit) {
        observers.add(observer)
    }

    fun toLiveData(): LiveData<T> {
        val mLiveData = MutableLiveData(value)
        observers.add {
            viewScope.launch {
                mLiveData.value = it
            }
        }
        return mLiveData.readOnly()
    }
}