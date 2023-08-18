package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class BooleanSetting(
    private val key: SettingsEnum,
    initial: Boolean
) : ISetting<Boolean>() {

    override val data = database.database.settingsBooleanValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { if (it != null) it == 1L else initial }
        .simpleStateIn(initial)

    override fun saveValue(newValue: Boolean) {
        database.database.settingsBooleanValuesQueries.insertOrUpdate(key.name, if (newValue) 1L else 0L)
    }

}