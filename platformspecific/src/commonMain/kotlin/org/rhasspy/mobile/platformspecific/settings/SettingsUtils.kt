package org.rhasspy.mobile.platformspecific.settings

import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

interface ISettingsUtils {

    suspend fun exportSettingsFile(): Boolean
    suspend fun restoreSettingsFromFile(): Boolean
    suspend fun shareSettingsFile(): Boolean

}

internal expect class SettingsUtils(
    externalResultRequest: IExternalResultRequest,
    nativeApplication: INativeApplication
) : ISettingsUtils {

    /**
     * export the settings file
     */
    override suspend fun exportSettingsFile(): Boolean

    /**
     * restore all settings from a file
     */
    override suspend fun restoreSettingsFromFile(): Boolean

    /**
     * share settings file but without sensitive data
     */
    override suspend fun shareSettingsFile(): Boolean

}