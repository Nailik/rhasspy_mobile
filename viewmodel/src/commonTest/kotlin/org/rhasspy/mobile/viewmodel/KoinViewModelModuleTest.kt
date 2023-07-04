package org.rhasspy.mobile.viewmodel

import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.settings.ISetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewStateCreator

class KoinViewModelModuleTest {

 //   @Test
    fun checkKoinDefinitions() {

        koinApplication {
            modules(viewModelModule())
            checkModules {
                withInstance(ISetting::class)
                withInstance(IService::class)
                withInstance(IConfigurationViewStateCreator::class)
            }
        }

    }

}