package org.rhasspy.mobile.platformspecific.external

expect object ExternalResultRequest {

    fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    suspend fun launchForPermission(permission: String): Boolean

}