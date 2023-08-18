package org.rhasspy.mobile.settings.types

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.simpleStateIn
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ISetting

class PorcupineKeywordDefaultListSetting(
    private val key: SettingsEnum,
    initial: ImmutableList<PorcupineDefaultKeyword>
) : ISetting<ImmutableList<PorcupineDefaultKeyword>>() {

    override val data = database.database.settingsPorcupineKeywordListValuesQueries
        .select(key.name)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { list ->
            list.map {
                PorcupineDefaultKeyword(
                    option = PorcupineKeywordOption.valueOf(it.value_),
                    isEnabled = it.enabled == 1L,
                    sensitivity = it.sensitivity.toFloat()
                )
            }.toTypedArray().toImmutableList()
        }
        .simpleStateIn(initial)

    override fun saveValue(newValue: ImmutableList<PorcupineDefaultKeyword>) {
        database.database.settingsPorcupineKeywordListValuesQueries.transaction {
            newValue.forEach {
                database.database.settingsPorcupineKeywordListValuesQueries
                    .insertOrUpdate(
                        id = key.name,
                        value_ = it.option.name,
                        enabled = if (it.isEnabled) 1 else 0,
                        sensitivity = it.sensitivity.toLong()
                    )
            }
        }
    }

}