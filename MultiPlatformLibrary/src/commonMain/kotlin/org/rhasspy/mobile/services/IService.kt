package org.rhasspy.mobile.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow

/**
 * Defines a Service
 *
 * handles start and stop of scope and start service link
 * also handles stop of scope and destroy of service link
 *
 * service link should be created in on start
 * onStop can be overwritten if in addition to scope and service link other tasks need to be stopped
 */
abstract class IService<T> {

    abstract val currentError: SharedFlow<ServiceError<T>?>
    private lateinit var scope: CoroutineScope
    private lateinit var serviceLink: IServiceLink

    fun start() {
        scope = CoroutineScope(Dispatchers.Default)
        //recreate service link

        serviceLink = onStart(scope)
        serviceLink.start(scope)

        onStarted(scope)
    }

    internal abstract fun onStart(scope: CoroutineScope) : IServiceLink

    fun stop() {
        onStop()

        if (::serviceLink.isInitialized) {
            serviceLink.destroy()
        }

        if (::scope.isInitialized) {
            scope.cancel()
        }

        onStopped()
    }

    open fun onStop() { }

    open fun onStopped() { }

    open fun onStarted(scope: CoroutineScope) { }

    fun restart() {
        stop()
        start()
    }

}