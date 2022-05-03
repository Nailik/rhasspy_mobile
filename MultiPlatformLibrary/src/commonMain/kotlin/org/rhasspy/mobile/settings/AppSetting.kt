package org.rhasspy.mobile.settings

import com.badoo.reaktive.observable.toObservable
import com.badoo.reaktive.subject.publish.PublishSubject
import dev.icerock.moko.mvvm.livedata.readOnly

/**
 * app settings immediately change when the data is changed
 */
class AppSetting<T>(key: SettingsEnum, initial: T) : Setting<T>(key, initial) {


    /**
     * data that's live updated to get current value readonly for view to show
     */
    var uiLiveData = value.readOnly()
}