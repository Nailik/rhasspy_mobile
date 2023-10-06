package org.rhasspy.mobile.logic.connections.rhasspy3wyoming

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.connections.IConnection
import org.rhasspy.mobile.logic.connections.http.IHttpConnection
import org.rhasspy.mobile.settings.ConfigurationSetting

internal interface IRhasspy3WyomingConnection : IConnection

/**
 * contains client to send data to http endpoints
 *
 * functions return the result or an exception
 */
internal class Rhasspy3WyomingConnection : IRhasspy3WyomingConnection, IHttpConnection(ConfigurationSetting.rhasspy3Connection) {

    override val logger = Logger.withTag("Rhasspy3WyomingConnection")

}