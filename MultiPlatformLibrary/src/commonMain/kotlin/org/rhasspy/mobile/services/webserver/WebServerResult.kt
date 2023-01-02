package org.rhasspy.mobile.services.webserver

sealed interface WebServerResult {

    object Ok : WebServerResult

    class Accepted(val data: String) : WebServerResult

    class Error(val errorType: WebServerServiceErrorType) : WebServerResult

}