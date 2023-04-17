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
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationUiEvent.Change.SetHttpSSLVerificationDisabled
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp.RemoteHermesHttpConfigurationViewState
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoteHermesHttpConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = RemoteHermesHttpConfigurationViewModel(
        service = HttpClientService(),
        testRunner = RemoteHermesHttpConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                RemoteHermesHttpConfigurationContent(viewModel)
            }
        }

    }

    /**
     * host is changed
     * disable ssl validation is on
     * switch is on
     *
     * user clicks switch
     * disable ssl validation is off
     * switch is off
     *
     * user click save
     * disable ssl validation off is saved
     * host is saved
     */
    @Test
    fun testHttpContent() = runBlocking {
        viewModel.onEvent(SetHttpSSLVerificationDisabled(true))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "textTestInput"

        //host is changed
        composeTestRule.onNodeWithTag(TestTag.Host).performScrollTo().performClick()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextReplacement(textInputTest)
        //disable ssl validation is on
        assertTrue { viewState.value.isHttpSSLVerificationDisabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performClick()
        //disable ssl validation is off
        assertFalse { viewState.value.isHttpSSLVerificationDisabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = RemoteHermesHttpConfigurationViewState()
        //disable ssl validation off is saved
        assertEquals(false, newViewState.isHttpSSLVerificationDisabled)
        //host is saved
        assertEquals(textInputTest, newViewState.httpClientServerEndpointHost)
    }
}