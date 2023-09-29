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
import org.rhasspy.mobile.ui.configuration.connections.MqttConnectionScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationUiEvent.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.connections.mqtt.MqttConnectionConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MqttConfigurationConnectionContentTest : FlakyTest() {

    private val viewModel = get<MqttConnectionConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        MqttConnectionScreen(viewModel)
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
    @AllowFlaky
    fun testMqttContent() = runTest {
        setupContent()

        viewModel.onEvent(SetMqttEnabled(false))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //MQTT disabled
        assertFalse { viewModel.viewState.value.editData.isEnabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).onListItemSwitch().assertIsOff()
        //MQTT Settings not visible
        composeTestRule.onNodeWithTag(TestTag.Host).assertDoesNotExist()
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
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isEnabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).onListItemSwitch().assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Host).assertExists()
        composeTestRule.onNodeWithTag(TestTag.UserName).assertExists()
        composeTestRule.onNodeWithTag(TestTag.Password).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).assertExists()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).assertExists()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).assertExists()

        //user click save
        composeTestRule.saveBottomAppBar()
        MqttConnectionConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //mqtt enabled saved
            assertEquals(true, it.isEnabled)
        }
    }

    /**
     * mqtt is enabled
     * host is changed
     * username is changed
     * password is changed
     *
     * user click save
     * host is saved
     * username is saved
     * password is saved
     */
    @Test
    @AllowFlaky
    fun testMqttConnectionSettings() = runTest {
        setupContent()

        viewModel.onEvent(SetMqttEnabled(true))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTestHost = "hostTestInput"
        val textInputTestUsername = "usernameTestInput"
        val textInputTestPassword = "passwordTestInput"

        //mqtt is enabled
        assertTrue { viewModel.viewState.value.editData.isEnabled }
        //host is changed
        composeTestRule.onNodeWithTag(TestTag.Host).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextInput(textInputTestHost)
        composeTestRule.awaitIdle()
        //username is changed
        composeTestRule.onNodeWithTag(TestTag.UserName).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.UserName).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.UserName).performTextInput(textInputTestUsername)
        composeTestRule.awaitIdle()
        //password is changed
        composeTestRule.onNodeWithTag(TestTag.Password).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Password).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.Password).performTextInput(textInputTestPassword)
        composeTestRule.awaitIdle()

        //user click save
        composeTestRule.saveBottomAppBar()
        composeTestRule.awaitIdle()
        MqttConnectionConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //host is saved
            assertEquals(textInputTestHost, it.host)
            //username is saved
            assertEquals(textInputTestUsername, it.userName)
            //password is saved
            assertEquals(textInputTestPassword, it.password)
        }
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
    @AllowFlaky
    fun testMqttSSL() = runTest {
        setupContent()

        viewModel.onEvent(SetMqttEnabled(true))
        viewModel.onEvent(SetMqttSSLEnabled(false))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //mqtt is enabled
        assertTrue { viewModel.viewState.value.editData.isEnabled }
        //ssl is disabled
        assertFalse { viewModel.viewState.value.editData.isSSLEnabled }
        //ssl is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()

        //user clicks ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is enabled
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isSSLEnabled }
        //ssl is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()

        //certificate button is shown
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user clicks save
        composeTestRule.saveBottomAppBar()
        MqttConnectionConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //ssl on is saved
            assertEquals(true, it.isSSLEnabled)
        }
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
    @AllowFlaky
    fun testMqttConnectionTiming() = runTest {
        setupContent()

        viewModel.onEvent(SetMqttEnabled(true))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTestConnectionTimeout = "498"
        val textInputTestKeepAliveInterval = "120"
        val textInputTestRetryInterval = "16504"

        //mqtt is enabled
        assertTrue { viewModel.viewState.value.editData.isEnabled }
        //timeout is changed
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout)
            .performTextReplacement(textInputTestConnectionTimeout)
        //keepAliveInterval is changed
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval)
            .performTextReplacement(textInputTestKeepAliveInterval)
        //retry interval is changed
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval)
            .performTextReplacement(textInputTestRetryInterval)

        //user click save
        composeTestRule.saveBottomAppBar()
        MqttConnectionConfigurationViewModel(get(), get()).viewState.value.editData.also {
            //timeout is saved
            assertEquals(textInputTestConnectionTimeout, it.connectionTimeoutText)
            //keepAliveInterval is saved
            assertEquals(textInputTestKeepAliveInterval, it.keepAliveIntervalText)
            //retry interval is saved
            assertEquals(textInputTestRetryInterval, it.retryIntervalText)
        }
    }

}