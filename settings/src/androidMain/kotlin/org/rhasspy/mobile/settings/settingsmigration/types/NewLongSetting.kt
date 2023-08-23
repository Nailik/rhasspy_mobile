package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewLongSetting(
    key: NewSettingsEnum,
    initial: Long
) : NewISetting<Long>(
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