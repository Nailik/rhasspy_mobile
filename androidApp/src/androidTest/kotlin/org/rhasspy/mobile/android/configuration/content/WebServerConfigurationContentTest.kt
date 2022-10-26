package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.viewModels.configuration.WebserverConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebServerConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = WebserverConfigurationViewModel()

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
        viewModel.toggleHttpServerEnabled(false)
        viewModel.toggleHttpServerSSLEnabled(false)
        viewModel.save()

        val textInputTest = "6541"

        //http api is disabled
        assertFalse { viewModel.isHttpServerEnabled.value }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).assertIsOff()
        //settings not visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).performClick()
        //http api is enabled
        assertTrue { viewModel.isHttpServerEnabled.value }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.ServerSwitch).assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Port).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()

        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.Port, true).onChild().performTextReplacement(textInputTest)

        //enable ssl is off
        assertFalse { viewModel.isHttpServerSSLEnabled.value }
        //enable ssl switch is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertIsOff()
        //certificate button not visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()

        //user clicks enable ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is on
        assertTrue { viewModel.isHttpServerSSLEnabled.value }
        //enable ssl switch is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertIsOn()
        //certificate button visible
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = WebserverConfigurationViewModel()
        //enable http api is saved
        assertEquals(true, newViewModel.isHttpServerEnabled.value)
        //port is saved
        assertEquals(textInputTest, newViewModel.httpServerPort.value)
        //enable ssl is saved
        assertEquals(true, newViewModel.isHttpServerSSLEnabled.value)
    }
}