package org.rhasspy.mobile.logic.connections.rhasspy3wyoming

import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.connections.IHttpConnection
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IRhasspy3WyomingConnection : KoinComponent

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
internal class Rhasspy3WyomingConnection : IRhasspy3WyomingConnection, IHttpConnection(ConfigurationSetting.rhasspy3Connection) {

    override val logger = LogType.HttpClientService.logger()

}