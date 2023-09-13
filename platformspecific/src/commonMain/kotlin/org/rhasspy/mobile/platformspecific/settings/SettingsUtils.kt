package org.rhasspy.mobile.platformspecific.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.database.IDriverFactory
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

interface ISettingsUtils {

    suspend fun exportSettingsFile(): Boolean
    suspend fun restoreSettingsFromFile(): Boolean
    suspend fun shareSettingsFile(toRemove: List<String>): Boolean

}

internal expect class SettingsUtils(
    externalResultRequest: IExternalResultRequest,
    nativeApplication: NativeApplication,
    databaseDriverFactory: IDriverFactory,
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
    override suspend fun shareSettingsFile(toRemove: List<String>): Boolean

}