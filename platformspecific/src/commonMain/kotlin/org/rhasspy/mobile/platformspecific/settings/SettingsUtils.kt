package org.rhasspy.mobile.platformspecific.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest

expect class SettingsUtils(
    externalResultRequest: ExternalResultRequest,
    nativeApplication: NativeApplication
) {

    /**
     * export the settings file
     */
    suspend fun exportSettingsFile(): Boolean

    /**
     * restore all settings from a file
     */
    suspend fun restoreSettingsFromFile(): Boolean

    /**
     * share settings file but without sensitive data
     */
    suspend fun shareSettingsFile(): Boolean

}