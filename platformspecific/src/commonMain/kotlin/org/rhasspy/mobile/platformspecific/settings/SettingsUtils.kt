package org.rhasspy.mobile.platformspecific.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.database.IDriverFactory
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest

interface ISettingsUtils {

    suspend fun exportSettingsFile(): Boolean
    suspend fun restoreSettingsFromFile(): RestoreResult
    suspend fun shareSettingsFile(): Boolean

}


enum class RestoreResult {
    Success,
    DeprecatedSuccess,
    Error
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
    override suspend fun restoreSettingsFromFile(): RestoreResult

    /**
     * share settings file but without sensitive data
     */
    override suspend fun shareSettingsFile(): Boolean

}