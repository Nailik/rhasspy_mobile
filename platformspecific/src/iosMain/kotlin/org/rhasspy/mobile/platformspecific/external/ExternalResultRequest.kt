package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class ExternalResultRequest actual constructor(
    private val nativeApplication: NativeApplication
) {

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