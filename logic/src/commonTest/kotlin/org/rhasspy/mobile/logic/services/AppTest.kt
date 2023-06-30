package org.rhasspy.mobile.logic.services

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.kodein.mock.tests.TestsWithMocks
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import kotlin.test.AfterTest

abstract class AppTest : KoinTest, TestsWithMocks() {

    @OptIn(ExperimentalCoroutinesApi::class)
    open fun before(module: Module) {
        injectMocksBeforeTest()

        Dispatchers.setMain(Dispatchers.Unconfined)
        startKoin {
            modules(
                logicModule,
                platformSpecificModule,
                module {
                    single { MapSettings() }
                },
                module
            )
        }

    }

    @AfterTest
    fun after() {
        stopKoin()
    }

}