package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.platformspecific.application.NativeApplication

expect class ExternalResultRequest(
    nativeApplication: NativeApplication
) {

    fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    suspend fun launchForPermission(permission: String): Boolean

}