package org.rhasspy.mobile.services

import kotlinx.coroutines.CoroutineScope

interface IServiceLink {

    fun start(scope: CoroutineScope)

    fun destroy()

}