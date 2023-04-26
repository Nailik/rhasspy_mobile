package org.rhasspy.mobile.platformspecific.external

expect object ExternalRedirect {

    fun <R> launch(intention: ExternalRedirectIntention<R>): ExternalRedirectResult<R>

    suspend fun <R> launchForResult(intention: ExternalRedirectIntention<R>): ExternalRedirectResult<R>

}