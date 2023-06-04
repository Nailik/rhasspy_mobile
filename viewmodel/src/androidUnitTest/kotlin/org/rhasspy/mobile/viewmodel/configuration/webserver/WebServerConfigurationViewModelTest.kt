package org.rhasspy.mobile.viewmodel.configuration.webserver

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class WebServerConfigurationViewModelTest : AppTest() {

    private lateinit var webServerConfigurationViewModel: WebServerConfigurationViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        webServerConfigurationViewModel = get()
    }

    @Test
    fun getScreen() {
    }

    @Test
    fun onEvent() {
    }

    @Test
    fun onSave() {
    }

    @Test
    fun onDiscard() {
    }
}