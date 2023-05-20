package org.rhasspy.mobile.platformspecific.external

actual object ExternalResultRequest {

    actual fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        //TODO("Not yet implemented")
        return ExternalRedirectResult.Success()
    }

    actual suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        //TODO("Not yet implemented")
        return ExternalRedirectResult.Success()
    }

    actual suspend fun launchForPermission(permission: String): Boolean {
        //TODO("Not yet implemented")
        return true
    }

}