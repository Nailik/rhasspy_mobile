package org.rhasspy.mobile.viewModels.configuration

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.serviceModule

abstract class IConfigurationViewModel : ViewModel(), KoinComponent {

    private val logger = Logger.withTag("IConfigurationViewModel")

    abstract val hasUnsavedChanges: StateFlow<Boolean>
    abstract val isTestingEnabled: StateFlow<Boolean>
    abstract val events: StateFlow<List<Event>>

    fun save() {
        onSave()

        unloadKoinModules(serviceModule)
        loadKoinModules(serviceModule)
    }

    abstract fun discard()

    abstract fun onSave()

    abstract fun runTest()

    abstract fun initializeTestParams()

    //TODO carefully test this works correctly
    fun onOpenTestPage() {
        //needs to be suspend, else ui thread is blocked
        CoroutineScope(Dispatchers.Default).launch {
            logger.e { "************* onOpenTestPage ************" }
            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)
            initializeTestParams()
        }
    }

    //TODO carefully test this works correctly
    fun stopTest() {
        //needs to be suspend, else ui thread is blocked
        CoroutineScope(Dispatchers.Default).launch {
            logger.e { "************* stopTest ************" }
            //reload koin modules when test is stopped
            unloadKoinModules(serviceModule)
            loadKoinModules(serviceModule)
        }
    }

}