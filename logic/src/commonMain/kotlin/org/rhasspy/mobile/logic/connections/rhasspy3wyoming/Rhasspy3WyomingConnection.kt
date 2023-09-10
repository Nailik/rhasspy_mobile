package org.rhasspy.mobile.logic.connections.rhasspy3wyoming

import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.IHttpConnection
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IRhasspy3WyomingConnection : IConnection

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
internal class Rhasspy3WyomingConnection : IRhasspy3WyomingConnection, IHttpConnection(ConfigurationSetting.rhasspy3Connection) {

    override val logger = LogType.HttpClientService.logger()

    override val connectionState = MutableStateFlow<ServiceState>(ServiceState.Pending)

}