package org.rhasspy.mobile.android.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.onNodeWithTag

/**
 * tests library container and library dialog
 */
class LibrariesContainerTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            LibrariesContainer(
                libraries = persistentListOf(),
                modifier = Modifier.fillMaxSize()
            )
        }

    }

    /**
     * Libraries container exists
     * User clicks library
     * Dialog opens
     * User clicks ok
     * Dialog closes
     */
    @Test
    fun testDialog() {
        //Libraries container exists
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).assertExists()
        //User clicks library
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).performClick()
        //Dialog opens
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertDoesNotExist()
    }

}