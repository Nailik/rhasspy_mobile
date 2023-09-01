package org.rhasspy.mobile.settings

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.rhasspy.mobile.settings.repositories.HttpConnectionSettingRepository
import org.rhasspy.mobile.settings.repositories.IHttpConnectionSettingRepository

val settingsModule = module {
    singleOf(::ISettingsDatabase)
    singleOf<IHttpConnectionSettingRepository>(::HttpConnectionSettingRepository)
}