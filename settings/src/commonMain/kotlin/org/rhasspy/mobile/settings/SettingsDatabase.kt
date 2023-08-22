package org.rhasspy.mobile.settings

import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsDatabase : KoinComponent {

    val database = Database(get<IDriverFactory>().createDriver())

}