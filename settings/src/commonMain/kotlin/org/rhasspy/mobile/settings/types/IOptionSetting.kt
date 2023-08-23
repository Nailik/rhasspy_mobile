package org.rhasspy.mobile.settings.types

import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class IOptionSetting<T>(
    key: SettingsEnum,
    initial: T
) : ISetting<T>(
    key = key,
    initial = initial
) where T : IOption<T>, T : Enum<T> {

    override fun readValue(): T {
        return database.settingsStringValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                if (it != null) initial.findValue(it) else initial
            }
    }

    override fun saveValue(newValue: T) {
        database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue.name)
    }

}