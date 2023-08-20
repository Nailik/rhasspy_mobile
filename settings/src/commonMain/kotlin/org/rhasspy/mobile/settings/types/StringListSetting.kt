package org.rhasspy.mobile.settings.types

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.settings.ISetting

class StringListSetting(
    private val key: SettingsEnum,
    initial: ImmutableList<String>
) : ISetting<ImmutableList<String>>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): ImmutableList<String> {
        return database.database.settingsStringListValuesQueries
            .select(key.name)
            .executeAsList()
            .toImmutableList()
    }

    override fun saveValue(newValue: ImmutableList<String>) {
        data.value = newValue
        database.database.settingsStringListValuesQueries.transaction {
            newValue.forEach {
                database.database.settingsStringListValuesQueries.insertOrUpdate(key.name, it)
            }
        }
    }

}