package org.rhasspy.mobile.settings

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.database.IDriverFactory

class ISettingsDatabase : KoinComponent {

    val database = SettingsDatabase(get<IDriverFactory>().createDriver(SettingsDatabase.Schema, "settings.db"))

}