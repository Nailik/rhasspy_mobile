package org.rhasspy.mobile.services

import io.ktor.utils.io.core.Closeable
import org.koin.core.component.KoinComponent

abstract class IService : KoinComponent, Closeable {

    override fun close() {
        onClose()
    }

    open fun onClose() {}

}