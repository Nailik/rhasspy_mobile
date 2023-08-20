package org.rhasspy.mobile.settings.types

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class IOptionSetting<T>(
    private val key: SettingsEnum,
    private val initial: T
) : ISetting<T>() where T : IOption<T>, T : Enum<T> {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): T {
        return database.database.settingsStringValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                if (it != null) initial.findValue(it) else initial
            }
    }

    override fun saveValue(newValue: T) {
        data.value = newValue
        database.database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue.name)
    }

}