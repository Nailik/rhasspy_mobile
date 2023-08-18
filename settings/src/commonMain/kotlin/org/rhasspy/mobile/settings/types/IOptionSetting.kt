package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.settings.ISetting

class IOptionSetting<R, T : IOption<R>>(
    private val key: SettingsEnum,
    initial: T
) : ISetting<T>() {

    override val data = database.database.settingsStringValuesQueries
        .select(key.name)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { if (it != null) initial.findValue(it) as T else initial }
        .simpleStateIn(initial)

    override fun saveValue(newValue: T) {
        database.database.settingsStringValuesQueries.insertOrUpdate(key.name, newValue.name)
    }

}