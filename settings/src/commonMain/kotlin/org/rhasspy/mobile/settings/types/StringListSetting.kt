package org.rhasspy.mobile.settings.types

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class StringListSetting(
    key: SettingsEnum,
    initial: ImmutableList<String>
) : ISetting<ImmutableList<String>>(
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