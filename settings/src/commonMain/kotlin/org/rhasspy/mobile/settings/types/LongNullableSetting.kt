package org.rhasspy.mobile.settings.types

import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class LongNullableSetting(
    key: SettingsEnum,
    initial: Long?
) : ISetting<Long?>(
    key = key,
    initial = initial
) {

    override fun readValue(): Long? {
        return database.settingsLongNullableValuesQueries
            .select(key.name)
            .executeAsOneOrNull()?.value_
    }

    override fun saveValue(newValue: Long?) {
        database.settingsLongNullableValuesQueries.insertOrUpdate(key.name, newValue)
    }

}