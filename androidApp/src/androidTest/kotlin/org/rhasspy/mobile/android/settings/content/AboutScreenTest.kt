package org.rhasspy.mobile.android.settings.content

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.AboutScreen
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag

class AboutScreenTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                AboutScreen()
            }
        }

    }

    /**
     * User clicks button
     * Dialog with text opens
     * User clicks ok button
     * Dialog closes
     */
    @Test
    fun testChangelogDialog() = runTest {
        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.DialogChangelogButton).performClick()
        //Dialog with text opens
        composeTestRule.onNodeWithTag(TestTag.DialogChangelog).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.onNodeWithTag(TestTag.DialogChangelog).assertDoesNotExist()
    }


    /**
     * User clicks button
     * Dialog with text opens
     * User clicks ok button
     * Dialog closes
     */
    @Test
    fun testPrivacyDialog() = runTest {
        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacyButton).performClick()
        //Dialog with text opens
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacy).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
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
    fun testLibraryDialog() = runTest {
        //Libraries container exists
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).assertExists()
        //User clicks library
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).onChildAt(0).performClick()
        //Dialog opens
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertDoesNotExist()
    }

}