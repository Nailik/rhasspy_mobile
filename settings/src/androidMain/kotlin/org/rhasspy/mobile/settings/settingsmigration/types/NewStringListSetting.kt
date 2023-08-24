package org.rhasspy.mobile.settings.settingsmigration.types

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.settings.settingsmigration.NewISetting
import org.rhasspy.mobile.settings.settingsmigration.NewSettingsEnum

class NewStringListSetting(
    key: NewSettingsEnum,
    initial: ImmutableList<String>
) : NewISetting<ImmutableList<String>>(
    key = key,
    initial = initial
) {

    override fun readValue(): ImmutableList<String> {
        return database.settingsStringListValuesQueries
            .select(key.name)
            .executeAsList()
            .toImmutableList()
    }

    override fun saveValue(newValue: ImmutableList<String>) {
        database.settingsStringListValuesQueries.transaction {
            newValue.forEach {
                database.settingsStringListValuesQueries.insertOrUpdate(key.name, it)
            }
        }
    }

}