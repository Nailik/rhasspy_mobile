package org.rhasspy.mobile.platformspecific.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.database.IDriverFactory
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

internal actual class SettingsUtils actual constructor(
    externalResultRequest: IExternalResultRequest,
    nativeApplication: NativeApplication,
    databaseDriverFactory: IDriverFactory,
) : ISettingsUtils {

    /**
     * export the settings file
     */
    actual override suspend fun exportSettingsFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * restore all settings from a file
     */
    actual override suspend fun restoreSettingsFromFile(): RestoreResult {
        //TODO("Not yet implemented")
        return RestoreResult.Success
    }

    /**
     * share settings file but without sensitive data
     */
    actual override suspend fun shareSettingsFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }
}