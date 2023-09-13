package org.rhasspy.mobile.testutils

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.setMain
import org.kodein.mock.tests.TestsWithMocks
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.database.IDriverFactory
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import org.rhasspy.mobile.settings.settingsModule
import org.rhasspy.mobile.viewmodel.viewModelModule
import kotlin.test.AfterTest

expect abstract class IAppTest() : TestsWithMocks

abstract class AppTest : IAppTest(), KoinTest {

    override fun setUpMocks() {}

    @OptIn(ExperimentalCoroutinesApi::class)
    open fun before(module: Module) {
        injectMocksBeforeTest()

        val application = object : NativeApplication() {
            override val isHasStarted: StateFlow<Boolean>
                get() = MutableStateFlow(true)

            override suspend fun resume() {}

            override fun onCreated() {}
        }

        Dispatchers.setMain(Dispatchers.Unconfined)
        startKoin {
            modules(
                platformSpecificModule,
                settingsModule(),
                logicModule(),
                viewModelModule(),
                module {
                    single<Settings> {
                        MapSettings()
                    }
                    single<IDriverFactory> {
                        TestDriverFactory()
                    }
                    single {
                        application
                    }
                    single<IDispatcherProvider> {
                        TestDispatcherProvider()
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