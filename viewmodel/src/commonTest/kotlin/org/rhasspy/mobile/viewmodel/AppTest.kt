package org.rhasspy.mobile.viewmodel

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import kotlin.test.AfterTest

abstract class AppTestNew : IAppTestNew(), KoinTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    open fun before(module: Module) {
        injectMocksBeforeTest()

        initApplication()

        Dispatchers.setMain(Dispatchers.Unconfined)
        startKoin {
            modules(
                logicModule,
                viewModelModule,
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