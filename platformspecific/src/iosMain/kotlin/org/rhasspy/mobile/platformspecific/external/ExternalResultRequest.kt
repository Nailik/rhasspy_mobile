package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.platformspecific.application.NativeApplication

internal actual class ExternalResultRequest actual constructor(
    private val nativeApplication: NativeApplication
) : IExternalResultRequest {

    actual override fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        //TODO #260
        return ExternalRedirectResult.Success()
    }

    actual override suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R> {
        //TODO #260
        return ExternalRedirectResult.Success()
    }

    actual override suspend fun launchForPermission(permission: String): Boolean {
        //TODO #260
        return true
    }

}