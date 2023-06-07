package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.remotehermeshttp.RemoteHermesHttpConfigurationEditViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class RemoteHermesHttpConfigurationViewModelTest : AppTest() {

    private lateinit var remoteHermesHttpConfigurationViewModel: RemoteHermesHttpConfigurationEditViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        remoteHermesHttpConfigurationViewModel = get()
    }

    @Test
    fun getScreen() {
    }

    @Test
    fun onEvent() {
    }

    @Test
    fun onDiscard() {
    }

    @Test
    fun onSave() {
    }
}