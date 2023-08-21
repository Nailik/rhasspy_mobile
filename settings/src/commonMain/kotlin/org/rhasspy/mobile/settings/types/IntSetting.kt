package org.rhasspy.mobile.settings.types

import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class IntSetting(
    key: SettingsEnum,
    initial: Int
) : ISetting<Int>(
    key = key,
    initial = initial
) {

    override fun readValue(): Int {
        return database.settingsLongValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.toInt() ?: initial
            }
    }

    override fun saveValue(newValue: Int) {
        database.settingsLongValuesQueries.insertOrUpdate(key.name, newValue.toLong())
    }

}