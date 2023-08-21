package org.rhasspy.mobile.settings.types

import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class LongSetting(
    key: SettingsEnum,
    initial: Long
) : ISetting<Long>(
    key = key,
    initial = initial
) {

    override fun readValue(): Long {
        return database.settingsLongValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it ?: initial
            }
    }

    override fun saveValue(newValue: Long) {
        database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue)
    }

}