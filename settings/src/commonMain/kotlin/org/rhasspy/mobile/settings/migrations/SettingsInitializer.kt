package org.rhasspy.mobile.settings.migrations

import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.platformspecific.extensions.commonExists
import org.rhasspy.mobile.settings.AppSetting

object SettingsInitializer : KoinComponent {

    private val migrations = listOf<IMigration>(

    )

    private fun initializeVersionIfMissing() {
        //1. App newly installed/ no settings
        //2. Settings without version
        //3. Settings with older version
        if ("shared_prefs/org.rhasspy.mobile.android_preferences.xml".toPath().commonExists()) {
            AppSetting.version.value = Migrate0To1().migrateIfNecessary(0)
        }
    }

    fun initialize() {
        initializeVersionIfMissing()
        migrations.forEach {
            //update from current to new version
            AppSetting.version.value = it.migrateIfNecessary(AppSetting.version.value)
        }
    }

}