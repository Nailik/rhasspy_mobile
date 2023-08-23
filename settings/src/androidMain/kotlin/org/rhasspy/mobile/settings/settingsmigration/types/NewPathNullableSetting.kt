package org.rhasspy.mobile.settings.settingsmigration.types

import okio.Path
import okio.Path.Companion.toPath
import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewPathNullableSetting(
    key: NewSettingsEnum,
    initial: Path?
) : NewISetting<Path?>(
    key = key,
    initial = initial
) {

    override fun readValue(): Path? {
        return database.settingsStringNullableValuesQueries
            .select(key.name)
            .executeAsOneOrNull().let {
                it?.value_?.toPath() ?: initial
            }
    }

    override fun saveValue(newValue: Path?) {
        database.settingsStringNullableValuesQueries.insertOrUpdate(key.name, newValue?.toString())
    }

}