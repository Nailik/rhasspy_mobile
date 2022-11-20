package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.rhasspy.mobile.ServiceTestName

abstract class IService<T>(
    params: T,
    private val isTest: Boolean,
    private val serviceName: ServiceTestName
) : KoinComponent {

    internal var params: T = params
        private set

    private val logger = Logger.withTag("$serviceName${if (isTest) "Test" else ""}")

    abstract fun onStart(scope: CoroutineScope)

    abstract fun onStop()


    private lateinit var scope: CoroutineScope

    fun start() {
        logger.v { "start" }

        //create scope
        scope = CoroutineScope(Dispatchers.Default)

        if (isTest) {
            //stop normal service
            get<IService<T>>(named(serviceName)).stop()
        } else {
            //load settings from configuration
            params = loadParamsFromConfiguration()
        }

        //run
        onStart(scope)
    }

    fun stop() {
        logger.v { "stop" }

        //stop
        onStop()

        if (::scope.isInitialized) {
            scope.cancel()
        }

        if (isTest) {
            //start normal service
            get<IService<T>>(named(serviceName)).start()
        }
    }

    abstract fun loadParamsFromConfiguration(): T

}