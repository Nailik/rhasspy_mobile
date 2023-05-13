package org.rhasspy.mobile.platformspecific.external

actual object ExternalRedirect {

    actual fun <R> launch(intention: ExternalRedirectIntention<R>): ExternalRedirectResult<R> {
        //TODO("Not yet implemented")
        return ExternalRedirectResult.Success()
    }

    actual suspend fun <R> launchForResult(intention: ExternalRedirectIntention<R>): ExternalRedirectResult<R> {
        //TODO("Not yet implemented")
        return ExternalRedirectResult.Success()
    }

}