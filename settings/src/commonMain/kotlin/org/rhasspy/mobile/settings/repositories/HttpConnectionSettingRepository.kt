package org.rhasspy.mobile.settings.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import database.SettingsHttpConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.connection.HttpConnectionParams
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ISettingsDatabase

interface IHttpConnectionSettingRepository {

    fun addOrUpdateHttpConnection(connection: HttpConnectionParams)

    fun removeHttpConnection(connection: HttpConnectionParams)

    suspend fun getHttpConnection(id: Long): StateFlow<HttpConnectionParams>

    fun getAllHttpConnections(): Flow<List<HttpConnectionParams>>


}

class HttpConnectionSettingRepository internal constructor() : IHttpConnectionSettingRepository, KoinComponent {

    val database = get<ISettingsDatabase>().database

    override fun addOrUpdateHttpConnection(connection: HttpConnectionParams) {
        with(connection) {
            database.settingsHttpConnectionsQueries.insertOrUpdate(
                id = id,
                host = host,
                timeout = timeout,
                bearerToken = bearerToken,
                isSslVerificationDisabled = if (isSSLVerificationDisabled) 1L else 0L,
            )
        }
    }

    override fun removeHttpConnection(connection: HttpConnectionParams) {
        connection.id?.also {
            database.settingsHttpConnectionsQueries.remove(it)
        }
    }

    override suspend fun getHttpConnection(id: Long): StateFlow<HttpConnectionParams> {
        return database.settingsHttpConnectionsQueries.select()
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .stateIn(CoroutineScope(Dispatchers.IO))
            .mapReadonlyState { it.mapToHttpConnection() }
    }

    override fun getAllHttpConnections(): Flow<List<HttpConnectionParams>> {
        return database.settingsHttpConnectionsQueries.select()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.mapToHttpConnection() } }
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