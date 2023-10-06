package org.rhasspy.mobile.logic.connections.webserver

internal sealed interface WebServerResult {

    data object Ok : WebServerResult

    class Accepted(val data: String) : WebServerResult

    class Error(val errorType: WebServerConnectionErrorType) : WebServerResult

}