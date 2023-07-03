package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.platformspecific.application.INativeApplication

internal actual class ExternalResultRequest actual constructor(
    private val nativeApplication: INativeApplication
) : IExternalResultRequest {

    actual override fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        //TODO("Not yet implemented")
        return ExternalRedirectResult.Success()
    }

    actual override suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        //TODO("Not yet implemented")
        return ExternalRedirectResult.Success()
    }

    actual override suspend fun launchForPermission(permission: String): Boolean {
        //TODO("Not yet implemented")
        return true
    }

}