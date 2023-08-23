package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewIOptionSetting<T>(
    key: NewSettingsEnum,
    initial: T
) : NewISetting<T>(
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