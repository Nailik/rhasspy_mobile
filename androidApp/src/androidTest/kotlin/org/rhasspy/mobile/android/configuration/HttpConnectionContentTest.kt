package org.rhasspy.mobile.android.configuration

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
import org.rhasspy.mobile.ui.configuration.connection.HttpConnectionScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationUiEvent.Change.SetHttpSSLVerificationDisabled
import org.rhasspy.mobile.viewmodel.configuration.connections.http.detail.HttpConnectionDetailConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpConnectionContentTest : FlakyTest() {

    private val viewModel = get<HttpConnectionDetailConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        HttpConnectionScreen()
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
    @AllowFlaky
    fun testHttpContent() = runTest {
        setupContent()

        viewModel.onEvent(SetHttpSSLVerificationDisabled(true))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTest = "textTestInput"

        //host is changed
        composeTestRule.onNodeWithTag(TestTag.Host).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextInput(textInputTest)
        //disable ssl validation is on
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isHttpSSLVerificationDisabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performClick()
        //disable ssl validation is off
        composeTestRule.awaitIdle()
        assertFalse { viewModel.viewState.value.editData.isHttpSSLVerificationDisabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()

        //user click save
        composeTestRule.saveBottomAppBar()
        HttpConnectionDetailConfigurationViewModel(get()).viewState.value.editData.also {
            //disable ssl validation off is saved
            assertEquals(false, it.isHttpSSLVerificationDisabled)
            //host is saved
            assertEquals(textInputTest, it.httpClientServerEndpointHost)
        }
    }
}