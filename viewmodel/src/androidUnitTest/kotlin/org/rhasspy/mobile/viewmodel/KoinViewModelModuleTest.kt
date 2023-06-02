package org.rhasspy.mobile.viewmodel

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import org.rhasspy.mobile.settings.ISetting
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class)
class KoinViewModelModuleTest {

    @Test
    fun checkKoinDefinitions() {
        viewModelModule.verify(extraTypes = listOf(ISetting::class))
    }

}