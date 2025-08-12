package org.rhasspy.mobile.platformspecific.external

import org.rhasspy.mobile.platformspecific.application.NativeApplication

interface IExternalResultRequest {

    fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    suspend fun launchForPermission(permission: String): Boolean

}

internal expect class ExternalResultRequest(
    nativeApplication: NativeApplication,
) : IExternalResultRequest {

    override fun <R> launch(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    override suspend fun <R> launchForResult(intention: ExternalResultRequestIntention<R>): ExternalRedirectResult<R>

    override suspend fun launchForPermission(permission: String): Boolean

}