package org.rhasspy.mobile.android.about

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onNodeWithTag

/**
 * Test visibility and close button of data privacy dialog
 */
class DataPrivacyDialogTest : FlakyTest() {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            DataPrivacyDialogButton("")
        }

    }

    /**
     * User clicks button
     * Dialog with text opens
     * User clicks ok button
     * Dialog closes
     */
    @Test
    fun testDialog() {
        //User clicks button
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacyButton).performClick()
        //Dialog with text opens
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacy).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.onNodeWithTag(TestTag.DialogDataPrivacy).assertDoesNotExist()
    }

}