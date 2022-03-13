package org.rhasspy.mobile.services.httpApi

import org.rhasspy.mobile.services.native.NativeCall

data class HttpCallWrapper(val route: String, val method: HttpMethodWrapper, val body: suspend NativeCall.() -> Unit)