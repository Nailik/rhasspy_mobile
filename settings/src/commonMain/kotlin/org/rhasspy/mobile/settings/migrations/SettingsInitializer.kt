package org.rhasspy.mobile.settings.migrations

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonExists
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath

object SettingsInitializer : KoinComponent {

    const val currentSettingsVersion = 3
    private val logger = Logger.withTag("SettingsInitializer")
    private val settings = get<Settings>()

    private val migrations = listOf(Migrate1To2, Migrate2To3)

    private fun initializeVersionIfMissing() {
        //1. App newly installed/ no settings
        //2. Settings without version
        //3. Settings with older version
        if (Path.commonInternalPath(get<NativeApplication>(), "shared_prefs/org.rhasspy.mobile.android_preferences.xml").commonExists()) {
            val currentVersionName = settings[SettingsEnum.Version.name, -1]
            logger.d { "initializeVersionIfMissing currentVersionName $currentVersionName" }
            if (currentVersionName == -1) {
                settings[SettingsEnum.Version.name] = Migrate0To1.migrateIfNecessary(0)
            }
        }
    }

    fun initialize() {
        logger.d { "initialize" }
        initializeVersionIfMissing()
        migrations.forEach {
            //update from current to new version
            settings[SettingsEnum.Version.name] = it.migrateIfNecessary(settings[SettingsEnum.Version.name, currentSettingsVersion])
        }
    }

}