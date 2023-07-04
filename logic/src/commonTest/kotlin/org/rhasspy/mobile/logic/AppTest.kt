package org.rhasspy.mobile.logic

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.kodein.mock.tests.TestsWithMocks
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import kotlin.test.AfterTest

expect abstract class IAppTest() : TestsWithMocks

abstract class AppTest : IAppTest(), KoinTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    open fun before(module: Module) {
        injectMocksBeforeTest()

        initApplication()

        Dispatchers.setMain(Dispatchers.Unconfined)
        startKoin {
            modules(
                platformSpecificModule,
                logicModule,
                module {
                    single<Settings> {
                        MapSettings()
                    }
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