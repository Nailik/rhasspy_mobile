package org.rhasspy.mobile.viewmodel

import com.russhwolf.settings.Settings
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.BeforeTest

abstract class ISettingsTest : KoinTest {

    @RelaxedMockK
    lateinit var settings: Settings

    open fun initModules(): MutableList<Module> {
        return mutableListOf(
            module {
                single { settings }
            }
        )
    }

    @BeforeTest
    open fun before() {
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
}