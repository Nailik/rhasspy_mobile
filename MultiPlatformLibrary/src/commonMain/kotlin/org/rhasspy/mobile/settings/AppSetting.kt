package org.rhasspy.mobile.settings

import dev.icerock.moko.mvvm.livedata.readOnly

/**
 * app settings immediately change when the data is changed
 */
class AppSetting<T>(key: SettingsEnum, initial: T) : Setting<T>(key, initial) {

    /**
     * data that's live updated to get current value readonly
     */
    var liveData = value.readOnly()

}