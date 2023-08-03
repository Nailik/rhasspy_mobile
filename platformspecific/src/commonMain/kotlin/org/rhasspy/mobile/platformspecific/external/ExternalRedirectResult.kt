package org.rhasspy.mobile.platformspecific.external

sealed interface ExternalRedirectResult<R> {
    class Success<R> : ExternalRedirectResult<R>
    data class Result<R>(val data: R) : ExternalRedirectResult<R>
    class NotFound<R> : ExternalRedirectResult<R>
    data class Error<R>(val cause: Throwable? = null) : ExternalRedirectResult<R>
}