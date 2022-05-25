package org.rhasspy.mobile.settings

import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.subject.publish.PublishSubject
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.launch

/**
 * app settings immediately change when the data is changed
 */
class AppSetting<T>(key: SettingsEnum, initial: T) : Setting<T>(key, initial) {

    /**
     * private subject to update the data
     */
    private val dataSubject = PublishSubject<T>()

    /**
     * data to observe for background things etc
     */
    var observableData = dataSubject.observeOn(ioScheduler)

    /**
     * data that's live updated to get current value readonly for view to show
     */
    var uiLiveData = value.readOnly()

    /**
     * represents current saved data and when changed, updates data asynchronously
     */
    var data: T
        get() = this.value.value
        set(newValue) {
            dataSubject.onNext(newValue)
            viewScope.launch {
                value.value = newValue
            }
        }

}