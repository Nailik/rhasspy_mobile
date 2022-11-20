package org.rhasspy.mobile.services

import org.koin.core.component.KoinComponent

abstract class IService : KoinComponent {
/*
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
*/
}