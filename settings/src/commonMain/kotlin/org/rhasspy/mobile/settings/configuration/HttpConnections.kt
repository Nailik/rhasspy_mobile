package org.rhasspy.mobile.settings.configuration

import org.rhasspy.mobile.data.connection.HttpConnection
import org.rhasspy.mobile.settings.ISettingObject

class HttpConnections internal constructor() : ISettingObject<List<HttpConnection>>() {

    override fun saveValue(newValue: List<HttpConnection>) {
        database.settingsHttpConnectionsQueries.transaction {
            newValue.forEach {
                database.settingsHttpConnectionsQueries.insertOrUpdate(
                    id = it.id,
                    host = it.host,
                    port = it.port?.toLong(),
                    timeout = it.timeout,
                    bearerToken = it.bearerToken,
                    isHermes = if (it.isHermes) 1L else 0L,
                    isWyoming = if (it.isWyoming) 1L else 0L,
                    isHomeAssistant = if (it.isHomeAssistant) 1L else 0L,
                    isSslVerificationDisabled = if (it.isSSLVerificationDisabled) 1L else 0L,
                )
            }
        }
    }

    override fun readValue(): List<HttpConnection> {
        return database.settingsHttpConnectionsQueries.select().executeAsList().map {
            HttpConnection(
                id = it.id,
                host = it.host,
                port = it.port?.toInt(),
                timeout = it.timeout,
                bearerToken = it.bearerToken,
                isHermes = it.isHermes == 1L,
                isWyoming = it.isWyoming == 1L,
                isHomeAssistant = it.isHomeAssistant == 1L,
                isSSLVerificationDisabled = it.isSslVerificationDisabled == 1L,
            )
        }
    }

}