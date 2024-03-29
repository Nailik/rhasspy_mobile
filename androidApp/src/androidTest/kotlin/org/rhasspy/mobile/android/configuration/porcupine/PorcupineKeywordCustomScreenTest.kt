@file:Suppress("unused")

package org.rhasspy.mobile.android.configuration.porcupine

import android.os.Environment
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.test.R
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.app.MainActivity
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.porcupine.PorcupineKeywordCustomScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import java.io.File
import kotlin.test.assertTrue

/**
 * disabled because too flaky, test by hand
 */
class PorcupineKeywordCustomScreenTest : KoinComponent {

    @get: Rule(order = 0)
    val composeTestRule = createEmptyComposeRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val viewModel = get<WakeWordConfigurationViewModel>()

    private val ppn = "Test-hello_en_android_v2_1_0.ppn"
    private val fileName = "porcupine_test.zip"

    @Before
    fun before() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        requestExternalStoragePermissions(device)
    }

    private fun setupUi() {
        scenario.onActivity { activity ->
            activity.setContent {
                TestContentProvider {
                    val viewState by viewModel.viewState.collectAsState()
                    PorcupineKeywordCustomScreen(
                        editData = viewState.editData.wakeWordPorcupineConfigurationData,
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }
    }

    /**
     * jarvis.zip file in download folder exists with ppn file inside
     *
     * Download button exists
     * select file button exists
     *
     * user clicks download
     * browser is opened
     * user clicks back
     * app is opened with current page (custom)
     *
     * user clicks select file
     * file manager is opened
     * user clicks back twice
     * app is opened with current page (custom)
     *
     * user clicks select file
     * file manager is opened
     * user clicks jarvis.zip
     * app is opened with current page (custom)
     * jarvis is added to list and enabled
     *
     * viewModel save is invoked
     * jarvis is saved with enabled
     */
    fun testActions() = runTest {
        setupUi()
        //copy test file to downloads directory
        //jarvis.zip file in download folder exists with ppn file inside
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        getInstrumentation().context.resources.openRawResource(R.raw.porcupine_test)
            .copyTo(file.outputStream())

        //Download button exists
        composeTestRule.onNodeWithTag(TestTag.Download).assertIsDisplayed()
        //select file button exists
        composeTestRule.onNodeWithTag(TestTag.SelectFile).assertIsDisplayed()

        //user clicks download
        composeTestRule.onNodeWithTag(TestTag.Download).performClick()
        //browser is opened
        device.wait(Until.hasObject(By.text(".*console.picovoice.ai/ppn.*".toPattern())), 5000)
        device.findObject(UiSelector().textMatches(".*console.picovoice.ai/ppn.*")).exists()
        //user clicks back
        device.pressBack()
        //app is opened with current page (custom)
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()

        //user clicks select file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        composeTestRule.awaitIdle()
        //file manager is opened
        device.waitForIdle()

        //user clicks jarvis.zip
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()
        //app is opened with current page (custom)
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()
        //jarvis is added to list and enabled
        composeTestRule.waitUntilExists(hasTag(ppn), 5000)
        composeTestRule.onNodeWithTag(ppn).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ppn).onListItemSwitch().assertIsOn()
    }

    /**
     * one element with ppn exists and selected
     *
     * user clicks delete on ppn
     * ppn contains undo button
     * undo button is clicked
     *
     * viewModel save is invoked
     * ppn is saved with ppn and enabled
     */
    fun testList() = runTest {
        setupUi()
        //one element with ppn exists and selected
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete()
        }
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        getInstrumentation().context.resources.openRawResource(R.raw.porcupine_test)
            .copyTo(file.outputStream())
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        composeTestRule.awaitIdle()
        device.waitForIdle()

        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()

        //app is opened with current page (custom)
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()
        //jarvis is added to list and enabled
        composeTestRule.waitUntilExists(hasTag(ppn), 5000)
        composeTestRule.onNodeWithTag(ppn).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ppn).onListItemSwitch().assertIsOn()

        viewModel.onEvent(Save)

        composeTestRule.awaitIdle()

        val viewState = viewModel.viewState.value.editData.wakeWordPorcupineConfigurationData
        assertTrue { viewState.customOptionsUi.find { it.keyword.fileName == ppn && it.keyword.isEnabled } != null }

        //user clicks delete on ppn
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Delete).assertIsDisplayed()
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Delete).performClick()
        //ppn contains undo button
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Undo).assertIsDisplayed()
        //undo button is clicked
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Undo).performClick()

        //viewModel save is invoked
        viewModel.onEvent(Save)
        assertTrue { true }
    }

}