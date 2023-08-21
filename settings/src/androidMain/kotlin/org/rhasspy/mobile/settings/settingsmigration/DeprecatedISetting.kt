package org.rhasspy.mobile.settings.settingsmigration

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
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.readOnly

private val logger = Logger.withTag("ISetting")

open class DeprecatedISetting<T>(
    private val key: SettingsEnum,
    private val initial: T,
    private val serializer: KSerializer<T>? = null
) : KoinComponent {

    private val settings = get<Settings>()

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
        try {
            if (serializer != null) {
                settings.encodeValue(serializer, key.name, newValue)
            } else {
                when (initial) {
                    is String     -> settings[key.name] = newValue as String
                    is Int        -> settings[key.name] = newValue as Int
                    is Float      -> settings[key.name] = newValue as Float
                    is Long?      -> settings[key.name] = newValue as Long?
                    is Long       -> settings[key.name] = newValue as Long
                    is Boolean    -> settings[key.name] = newValue as Boolean
                    is IOption<*> -> settings[key.name] = (newValue as IOption<*>).name
                    else          -> logger.a { "save value unsupported type initial: $initial key: ${key.name}" }
                }
            }
        } catch (e: Exception) {
            logger.a { "saveValue failed for ${key.name} with input $initial" }
        }
    }

    /**
     * reads current saved value
     */
    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private fun readValue(): T {
        return try {
            if (serializer != null) {
                settings.decodeValue(serializer, key.name, initial)
            } else {
                when (initial) {
                    is String     -> settings[key.name, initial]
                    is Int        -> settings[key.name, initial]
                    is Float      -> settings[key.name, initial]
                    is Long?      -> settings[key.name, initial ?: 0L]
                    is Long       -> settings[key.name, initial]
                    is Boolean    -> settings[key.name, initial]
                    is IOption<*> -> initial.findValue(settings[key.name, initial.name])
                    else          -> {
                        logger.a { "could not read ${key.name} resetting it to $initial" }
                        initial
                    }
                } as T
            }
        } catch (e: Exception) {
            logger.a { "reset of ${key.name} to $initial" }
            saveValue(initial)
            initial
        }
    }

}