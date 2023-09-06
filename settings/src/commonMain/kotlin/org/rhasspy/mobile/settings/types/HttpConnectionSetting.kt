package org.rhasspy.mobile.settings.types

import database.SettingsHttpConnection
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.settings.newtypes.ISettingNew

class HttpConnectionSetting internal constructor() : ISettingNew<HttpConnectionParams>() {

    init {
        saveValue(
            HttpConnectionParams(
                id = null,
                host = "",
                timeout = 10,
                bearerToken = null,
                isSSLVerificationDisabled = false,
            ), true
        )
    }

    override fun saveValue(newValue: HttpConnectionParams, ignoreIfExists: Boolean) {
        with(newValue) {
            database.settingsHttpConnectionsQueries.insertOrUpdate(
                id = id,
                host = host,
                timeout = timeout,
                bearerToken = bearerToken,
                isSslVerificationDisabled = if (isSSLVerificationDisabled) 1L else 0L,
            )
        }
    }

    override fun readValue(): HttpConnectionParams {
        return database.settingsHttpConnectionsQueries.select().executeAsOne().mapToHttpConnection()
    }

    private fun SettingsHttpConnection.mapToHttpConnection(): HttpConnectionParams {
        return HttpConnectionParams(
            id = id,
            host = host,
            timeout = timeout,
            bearerToken = bearerToken,
            isSSLVerificationDisabled = isSslVerificationDisabled == 1L,
        )
    }

}