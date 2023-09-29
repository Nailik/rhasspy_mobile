package org.rhasspy.mobile.android.configuration.connection

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.connections.WebServerConnectionScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change.SetHttpServerEnabled
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationUiEvent.Change.SetHttpServerSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.connections.webserver.WebServerConnectionConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebServerConnectionConfigurationContentTest : FlakyTest() {

    private val viewModel = get<WebServerConnectionConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        WebServerConnectionScreen(viewModel)
    }

    /**
     * http api is disabled
     * switch is off
     * settings not visible
     *
     * user clicks switch
     * http api is enabled
     * switch is pn
     * settings visible
     *
     * port is changed
     *
     * enable ssl is off
     * enable ssl switch is off
     * certificate button not visible
     *
     * user clicks enable ssl
     * ssl is on
     * enable ssl switch is on
     * certificate button visible
     *
     * user click save
     * enable http api is saved
     * port is saved
     * enable ssl is saved
     */
    @Test
    @AllowFlaky
    fun testHttpContent() = runTest {
        setupContent()

        viewModel.onEvent(SetHttpServerEnabled(false))
        viewModel.onEvent(SetHttpServerSSLEnabled(false))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTest = "6541"

        //http api is disabled
        assertFalse { viewModel.viewState.value.editData.isEnabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).onListItemSwitch().assertIsOff()
        //settings not visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).performClick()
        //http api is enabled
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isEnabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).onListItemSwitch().assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()

        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextReplacement(textInputTest)

        //enable ssl is off
        composeTestRule.awaitIdle()
        assertFalse { viewModel.viewState.value.editData.isSSLEnabled }
        //enable ssl switch is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()
        //certificate button not visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks enable ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is on
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isSSLEnabled }
        //enable ssl switch is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()
        //certificate button visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user click save
        composeTestRule.saveBottomAppBar()
        WebServerConnectionConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //enable http api is saved
            assertEquals(true, it.isEnabled)
            //port is saved
            assertEquals(textInputTest, it.portText)
            //enable ssl is saved
            assertEquals(true, it.isSSLEnabled)
        }
    }
}