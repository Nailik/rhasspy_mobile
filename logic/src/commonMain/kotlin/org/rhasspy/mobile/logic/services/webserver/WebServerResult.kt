package org.rhasspy.mobile.logic.services.webserver

sealed interface WebServerResult {

    data object Ok : WebServerResult

    class Accepted(val data: String) : WebServerResult

    class Error(val errorType: WebServerServiceErrorType) : WebServerResult

}