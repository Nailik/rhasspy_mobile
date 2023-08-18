package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class StringListSetting(
    private val key: SettingsEnum,
    initial: ImmutableList<String>
) : ISetting<ImmutableList<String>>() {

    override val data = database.database.settingsStringListValuesQueries
        .select(key.name)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { it.toImmutableList() }
        .simpleStateIn(initial)

    override fun saveValue(newValue: ImmutableList<String>) {
        database.database.settingsStringListValuesQueries.transaction {
            newValue.forEach {
                database.database.settingsStringListValuesQueries.insertOrUpdate(key.name, it)
            }
        }
    }

}