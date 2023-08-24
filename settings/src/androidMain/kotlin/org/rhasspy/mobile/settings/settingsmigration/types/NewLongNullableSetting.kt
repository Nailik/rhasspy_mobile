package org.rhasspy.mobile.settings.settingsmigration.types

import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewLongNullableSetting(
    key: NewSettingsEnum,
    initial: Long?
) : NewISetting<Long?>(
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