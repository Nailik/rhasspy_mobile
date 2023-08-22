package org.rhasspy.mobile.settings

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val settingsModule = module {
    single<IDriverFactory> { DriverFactory() }
    singleOf(::SettingsDatabase)
}