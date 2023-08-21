package org.rhasspy.mobile.settings.types

import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class StringSetting(
    key: SettingsEnum,
    initial: String
) : ISetting<String>(
    key = key,
    initial = initial
) {

    override fun readValue(): String {
        return database.settingsStringValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it ?: initial
            }
    }

    override fun saveValue(newValue: String) {
        database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue)
    }

}