package org.rhasspy.mobile.viewmodel

import com.russhwolf.settings.Settings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.logic.logicModule
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.platformSpecificModule
import kotlin.test.*

abstract class AppTest : KoinTest {

    @RelaxedMockK
    lateinit var settings: Settings

    @RelaxedMockK
    lateinit var nativeApplication: NativeApplication

    @OptIn(ExperimentalCoroutinesApi::class)
    open fun before(module: Module) {
        Dispatchers.setMain(Dispatchers.Unconfined)
        startKoin {
            modules(
                logicModule,
                viewModelModule,
                platformSpecificModule,
                module {
                    single { settings }
                    single { nativeApplication }
                },
                module
            )
        }

        MockKAnnotations.init(this, relaxUnitFun = false)

        val stringSlot = slot<String>()
        every { settings.getString(any(), capture(stringSlot)) } answers { stringSlot.captured }
        val intSlot = slot<Int>()
        every { settings.getInt(any(), capture(intSlot)) } answers { intSlot.captured }
        val floatSlot = slot<Float>()
        every { settings.getFloat(any(), capture(floatSlot)) } answers { floatSlot.captured }
        val longSlot = slot<Long>()
        every { settings.getLong(any(), capture(longSlot)) } answers { longSlot.captured }
        val booleanSlot = slot<Boolean>()
        every { settings.getBoolean(any(), capture(booleanSlot)) } answers { booleanSlot.captured }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

}