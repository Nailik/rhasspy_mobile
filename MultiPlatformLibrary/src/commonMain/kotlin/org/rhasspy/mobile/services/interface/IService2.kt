package org.rhasspy.mobile.services.`interface`

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.rhasspy.mobile.ServiceTestName

abstract class IService2(
    private val isTest: Boolean,
    private val serviceName: ServiceTestName) : KoinComponent {

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
            get<IService2>(named(serviceName)).stop()
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
            get<IService2>(named(serviceName)).start()
        }
    }

}