package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.settings.SettingsEnum
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.commonExists
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.settings.AppSetting

object SettingsInitializer : KoinComponent {

    private val migrations = listOf<IMigration>(

    )

    private fun initializeVersionIfMissing() {
        //1. App newly installed/ no settings
        //2. Settings without version
        //3. Settings with older version
        if (Path.commonInternalPath(get<NativeApplication>(), "shared_prefs/org.rhasspy.mobile.android_preferences.xml").commonExists()) {
            //TODO only if app version doesn't exist?
            if (get<Settings>()[SettingsEnum.Version.name, -1] == -1) {
                AppSetting.version.value = Migrate0To1().migrateIfNecessary(0)
            }
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