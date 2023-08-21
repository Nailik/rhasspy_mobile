package org.rhasspy.mobile.settings.types

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineKeywordOption
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ISetting

class PorcupineKeywordDefaultListSetting(
    key: SettingsEnum,
    initial: ImmutableList<PorcupineDefaultKeyword>
) : ISetting<ImmutableList<PorcupineDefaultKeyword>>(
    key = key,
    initial = initial
) {

    override fun readValue(): ImmutableList<PorcupineDefaultKeyword> {
        return database.settingsPorcupineKeywordListValuesQueries
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
        database.settingsPorcupineKeywordListValuesQueries.transaction {
            newValue.forEach {
                database.settingsPorcupineKeywordListValuesQueries
                    .insertOrUpdate(
                        id = key.name,
                        value = it.option.name,
                        enabled = if (it.isEnabled) 1 else 0,
                        sensitivity = it.sensitivity.toLong()
                    )
            }
        }
    }

}