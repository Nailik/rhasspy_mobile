package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.settings.AboutScreen
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenViewModel

class AboutScreenTest : FlakyTest() {

    private val viewModel = get<AboutScreenViewModel>()

    @Composable
    override fun ComposableContent() {
        AboutScreen(viewModel)
    }

    /**
     * User clicks button
     * Dialog with text opens
     * User clicks ok button
     * Dialog closes
     */
    @Test
    @AllowFlaky
    fun testChangelogDialog() = runTest {
        setupContent()
        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.DialogChangelogButton).performClick()
        //Dialog with text opens
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogChangelog).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogChangelog).assertDoesNotExist()
    }


    /**
     * User clicks button
     * Dialog with text opens
     * User clicks ok button
     * Dialog closes
     */
    @Test
    @AllowFlaky
    fun testPrivacyDialog() = runTest {
        setupContent()
        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacyButton).performClick()
        //Dialog with text opens
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacy).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacy).assertDoesNotExist()
    }

    /**
     * Libraries container exists
     * User clicks library
     * Dialog opens
     * User clicks ok
     * Dialog closes
     */
    @Test
    @AllowFlaky
    fun testLibraryDialog() = runTest {
        setupContent()
        //Libraries container exists
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).assertExists()
        //User clicks library
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).onChildAt(0).performClick()
        //Dialog opens
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertDoesNotExist()
    }

}