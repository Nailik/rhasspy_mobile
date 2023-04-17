package org.rhasspy.mobile.android.configuration.content.porcupine

import android.os.Environment
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.onNodeWithCombinedTag
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.android.requestExternalStoragePermissions
import org.rhasspy.mobile.android.test.R
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState
import java.io.File
import kotlin.test.assertTrue


class PorcupineKeywordCustomScreenTest : KoinComponent {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    private val viewModel = WakeWordConfigurationViewModel(
        service = WakeWordService(),
        testRunner = WakeWordConfigurationTest()
    )

    private val ppn = "Test-hello_en_android_v2_1_0.ppn"
    private val fileName = "porcupine_test.zip"

    @Before
    fun setUp() {
        requestExternalStoragePermissions(device)

        composeTestRule.activity.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                val viewState by viewModel.viewState.collectAsState()
                val contentViewState by viewState.editViewState.collectAsState()
                PorcupineKeywordCustomScreen(
                    viewState = contentViewState.wakeWordPorcupineViewState,
                    onAction = viewModel::onAction
                )
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
    @Test
    fun testActions() = runBlocking {
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
        device.findObject(UiSelector().textMatches(".*console.picovoice.ai/ppn.*")).exists()
        //user clicks back
        device.pressBack()
        //app is opened with current page (custom)
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()

        //user clicks select file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //file manager is opened
        device.findObject(UiSelector().textMatches(fileName)).exists()
        //user clicks back twice
        device.pressBack()

        if (get<NativeApplication>().isAppInBackground.value) {
            device.pressBack()
        }
        //app is opened with current page (custom)
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()

        //user clicks select file
        composeTestRule.onNodeWithTag(TestTag.SelectFile).performClick()
        //file manager is opened
        device.findObject(UiSelector().textMatches(fileName)).exists()
        //user clicks jarvis.zip
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()
        //app is opened with current page (custom)
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.PorcupineKeywordCustomScreen).assertIsDisplayed()
        //jarvis is added to list and enabled
        composeTestRule.onNodeWithTag(ppn).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ppn).onListItemSwitch().assertIsOn()

        //viewModel save is invoked
        viewModel.onSave()
        val newViewState = PorcupineViewState()
        //jarvis is saved with enabled
        assertTrue { newViewState.customOptionsUi.find { it.keyword.fileName == ppn && it.keyword.isEnabled } != null }
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
    @Test
    fun testList() = runBlocking {
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
        device.findObject(UiSelector().textMatches(fileName)).clickAndWaitForNewWindow()
        composeTestRule.awaitIdle()
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        val viewState = viewModel.viewState.value.editViewState.value.wakeWordPorcupineViewState
        assertTrue { viewState.customOptionsUi.find { it.keyword.fileName == ppn && it.keyword.isEnabled } != null }

        //user clicks delete on ppn
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Delete).assertIsDisplayed()
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Delete).performClick()
        //ppn contains undo button
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Undo).assertIsDisplayed()
        //undo button is clicked
        composeTestRule.onNodeWithCombinedTag(ppn, TestTag.Undo).performClick()

        //viewModel save is invoked
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = PorcupineViewState()
        //ppn is saved with ppn.ppn and enabled
        assertTrue { newViewState.customOptionsUi.find { it.keyword.fileName == ppn && it.keyword.isEnabled } != null }
    }

}