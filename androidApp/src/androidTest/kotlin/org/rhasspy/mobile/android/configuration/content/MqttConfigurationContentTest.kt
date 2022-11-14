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
import org.rhasspy.mobile.viewModels.configuration.MqttConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MqttConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = MqttConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                MqttConfigurationContent(viewModel)
            }
        }

    }

    /**
     * MQTT disabled
     * switch is off
     * MQTT Settings not visible
     *
     * User clicks switch
     * mqtt enabled
     * switch is on
     * settings visible
     *
     * user click save
     * mqtt enabled saved
     */
    @Test
    fun testMqttContent() = runBlocking {
        viewModel.toggleMqttEnabled(false)
        viewModel.save()

        //MQTT disabled
        assertFalse { viewModel.isMqttEnabled.value }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).assertIsOff()
        //MQTT Settings not visible
        composeTestRule.onNodeWithTag(TestTag.Host).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.Port).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.UserName).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.Password).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).assertDoesNotExist()

        //User clicks switch
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).performClick()
        //mqtt enabled
        assertTrue { viewModel.isMqttEnabled.value }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Host).assertExists()
        composeTestRule.onNodeWithTag(TestTag.Port).assertExists()
        composeTestRule.onNodeWithTag(TestTag.UserName).assertExists()
        composeTestRule.onNodeWithTag(TestTag.Password).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).assertExists()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).assertExists()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).assertExists()

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = MqttConfigurationViewModel()
        //mqtt enabled saved
        assertEquals(true, newViewModel.isMqttEnabled.value)
    }

    /**
     * mqtt is enabled
     * host is changed
     * port is changed
     * username is changed
     * password is changed
     *
     * user click save
     * host is saved
     * port is saved
     * username is saved
     * password is saved
     */
    @Test
    fun testMqttConnectionSettings() = runBlocking {
        viewModel.toggleMqttEnabled(true)
        viewModel.save()

        val textInputTestHost = "hostTestInput"
        val textInputTestPort = "portTestInput"
        val textInputTestUsername = "usernameTestInput"
        val textInputTestPassword = "passwordTestInput"

        //mqtt is enabled
        assertTrue { viewModel.isMqttEnabled.value }
        //host is changed
        composeTestRule.onNodeWithTag(TestTag.Host).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextReplacement(textInputTestHost)
        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextReplacement(textInputTestPort)
        //username is changed
        composeTestRule.onNodeWithTag(TestTag.UserName).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.UserName).performTextReplacement(textInputTestUsername)
        //password is changed
        composeTestRule.onNodeWithTag(TestTag.Password).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Password).performTextReplacement(textInputTestPassword)

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = MqttConfigurationViewModel()
        //host is saved
        assertEquals(textInputTestHost, newViewModel.mqttHost.value)
        //port is saved
        assertEquals(textInputTestPort, newViewModel.mqttPort.value)
        //username is saved
        assertEquals(textInputTestUsername, newViewModel.mqttUserName.value)
        //password is saved
        assertEquals(textInputTestPassword, newViewModel.mqttPassword.value)
    }

    /**
     * mqtt is enabled
     * ssl is disabled
     * ssl is off
     *
     * user clicks ssl
     * ssl is enabled
     * ssl is on
     *
     * certificate button is shown
     *
     * user clicks save
     * ssl on is saved
     */
    @Test
    fun testMqttSSL() = runBlocking {
        viewModel.toggleMqttEnabled(true)
        viewModel.toggleMqttSSLEnabled(false)
        viewModel.save()

        //mqtt is enabled
        assertTrue { viewModel.isMqttEnabled.value }
        //ssl is disabled
        assertFalse { viewModel.isMqttSSLEnabled.value }
        //ssl is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertIsOff()

        //user clicks ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performClick()
        //ssl is enabled
        assertTrue { viewModel.isMqttSSLEnabled.value }
        //ssl is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertIsOn()

        //certificate button is shown
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = MqttConfigurationViewModel()
        //ssl on is saved
        assertEquals(true, newViewModel.isMqttSSLEnabled.value)
    }

    /**
     * mqtt is enabled
     * timeout is changed
     * keepAliveInterval is changed
     * retry interval is changed
     *
     * user click save
     * timeout is saved
     * keepAliveInterval is saved
     * retry interval is saved
     */
    @Test
    fun testMqttConnectionTiming() = runBlocking {
        viewModel.toggleMqttEnabled(true)
        viewModel.save()

        val textInputTestConnectionTimeout = "498"
        val textInputTestKeepAliveInterval = "120"
        val textInputTestRetryInterval = "16504"

        //mqtt is enabled
        assertTrue { viewModel.isMqttEnabled.value }
        //timeout is changed
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).performTextReplacement(textInputTestConnectionTimeout)
        //keepAliveInterval is changed
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).performTextReplacement(textInputTestKeepAliveInterval)
        //retry interval is changed
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).performTextReplacement(textInputTestRetryInterval)

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = MqttConfigurationViewModel()
        //timeout is saved
        assertEquals(textInputTestConnectionTimeout, newViewModel.mqttConnectionTimeout.value)
        //keepAliveInterval is saved
        assertEquals(textInputTestKeepAliveInterval, newViewModel.mqttKeepAliveInterval.value)
        //retry interval is saved
        assertEquals(textInputTestRetryInterval, newViewModel.mqttRetryInterval.value)
    }

}