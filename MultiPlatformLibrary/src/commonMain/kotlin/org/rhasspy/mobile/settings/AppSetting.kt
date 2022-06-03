package org.rhasspy.mobile.settings

/**
 * app settings immediately change when the data is changed
 */
class AppSetting<T>(key: SettingsEnum, initial: T) : Setting<T>(key, initial) {

    override var value
        get() = data.value
        set(value) {
            data.value = value
        }

}