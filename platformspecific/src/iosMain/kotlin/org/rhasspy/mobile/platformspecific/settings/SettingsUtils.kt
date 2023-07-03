package org.rhasspy.mobile.platformspecific.settings

import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

internal actual class SettingsUtils actual constructor(
    externalResultRequest: IExternalResultRequest,
    nativeApplication: INativeApplication
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
    actual override suspend fun restoreSettingsFromFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * share settings file but without sensitive data
     */
    actual override suspend fun shareSettingsFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }
}