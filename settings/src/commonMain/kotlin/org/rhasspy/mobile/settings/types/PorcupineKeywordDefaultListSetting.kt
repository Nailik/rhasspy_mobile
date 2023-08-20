package org.rhasspy.mobile.settings.types

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ISetting

class PorcupineKeywordDefaultListSetting(
    private val key: SettingsEnum,
    initial: ImmutableList<PorcupineDefaultKeyword> //TODO initial
) : ISetting<ImmutableList<PorcupineDefaultKeyword>>() {

    override val data = MutableStateFlow(readInitial())

    private fun readInitial(): ImmutableList<PorcupineDefaultKeyword> {
        return database.database.settingsPorcupineKeywordListValuesQueries
            .select(key.name)
            .executeAsList().map {
                PorcupineDefaultKeyword(
                    option = PorcupineKeywordOption.valueOf(it.value_),
                    isEnabled = it.enabled == 1L,
                    sensitivity = it.sensitivity.toFloat()
                )
            }.toTypedArray().toImmutableList()
    }

    override fun saveValue(newValue: ImmutableList<PorcupineDefaultKeyword>) {
        data.value = newValue
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