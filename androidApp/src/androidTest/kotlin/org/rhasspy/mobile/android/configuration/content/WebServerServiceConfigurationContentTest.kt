package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiAction.Change.SetHttpServerEnabled
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationUiAction.Change.SetHttpServerSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.webserver.WebServerConfigurationViewState
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebServerServiceConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WebServerConfigurationViewModel(
        service = WebServerService(),
        testRunner = WebServerConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                WebServerConfigurationContent(viewModel)
            }
        }

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
    fun testHttpContent() = runBlocking {
        viewModel.onAction(SetHttpServerEnabled(false))
        viewModel.onAction(SetHttpServerSSLEnabled(false))
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "6541"

        //http api is disabled
        assertFalse { viewState.value.isHttpServerEnabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).onListItemSwitch().assertIsOff()
        //settings not visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).performClick()
        //http api is enabled
        assertTrue { viewState.value.isHttpServerEnabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).onListItemSwitch().assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()

        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextReplacement(textInputTest)

        //enable ssl is off
        assertFalse { viewState.value.isHttpServerSSLEnabled }
        //enable ssl switch is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()
        //certificate button not visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks enable ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is on
        assertTrue { viewState.value.isHttpServerSSLEnabled }
        //enable ssl switch is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()
        //certificate button visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = WebServerConfigurationViewState()
        //enable http api is saved
        assertEquals(true, newViewState.isHttpServerEnabled)
        //port is saved
        assertEquals(textInputTest, newViewState.httpServerPortText)
        //enable ssl is saved
        assertEquals(true, newViewState.isHttpServerSSLEnabled)
    }
}