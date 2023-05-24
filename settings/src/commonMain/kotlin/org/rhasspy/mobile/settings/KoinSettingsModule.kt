package org.rhasspy.mobile.settings

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val settingsModule = module {
    single {
        Settings()
    }
}