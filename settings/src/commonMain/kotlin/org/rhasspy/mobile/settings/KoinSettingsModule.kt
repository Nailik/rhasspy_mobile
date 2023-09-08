package org.rhasspy.mobile.settings

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun settingsModule() = module {
    singleOf(::Settings)
}