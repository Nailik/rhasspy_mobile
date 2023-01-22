package org.rhasspy.mobile.settings

import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.option.IOption

private val settings = Settings()
private val logger = Logger.withTag("ISetting")

open class ISetting<T>(
    private val key: SettingsEnum,
    private val initial: T,
    private val serializer: KSerializer<T>? = null
) {

    /**
     * data used to get current saved value or to set value for unsaved changes
     */
    private val _data = MutableStateFlow(readValue())
    val data = _data.readOnly

    var value: T = readValue()
        get() {
            return _data.value
        }
        set(value) {
            saveValue(value)
            _data.value = value
            field = value
        }

    /**
     * save current value
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private fun saveValue(newValue: T) {
        when (initial) {
            is String -> settings[key.name] = newValue as String
            is Int -> settings[key.name] = newValue as Int
            is Float -> settings[key.name] = newValue as Float
            is Boolean -> settings[key.name] = newValue as Boolean
            is IOption<*> -> settings[key.name] = (newValue as IOption<*>).name
            else -> serializer?.let {
                settings.encodeValue(serializer, key.name, newValue)
            } ?: run {
                logger.a { "save value unsupported type initial: $initial key: ${key.name}" }
            }
        }
    }

    /**
     * reads current saved value
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    @Suppress("UNCHECKED_CAST")
    private fun readValue(): T {
        return when (initial) {
            is String -> settings[key.name, initial]
            is Int -> settings[key.name, initial]
            is Float -> settings[key.name, initial]
            is Boolean -> settings[key.name, initial]
            is IOption<*> -> initial.findValue(settings[key.name, initial.name])
            else -> serializer?.let {
                settings.decodeValue(serializer, key.name, initial)
            } ?: run {
                initial
            }
        } as T
    }

}