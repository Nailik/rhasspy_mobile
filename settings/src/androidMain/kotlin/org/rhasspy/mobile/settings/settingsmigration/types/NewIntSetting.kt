package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewIntSetting(
    key: NewSettingsEnum,
    initial: Int
) : NewISetting<Int>(
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