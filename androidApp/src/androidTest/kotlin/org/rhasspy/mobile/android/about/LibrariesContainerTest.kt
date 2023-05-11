package org.rhasspy.mobile.android.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import com.mikepenz.aboutlibraries.entity.Library
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.libraries.StableLibrary

/**
 * tests library container and library dialog
 */
class LibrariesContainerTest : FlakyTest() {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            LibrariesContainer(
                libraries = persistentListOf(
                    StableLibrary(
                        library = Library(
                            uniqueId = "uniqueId",
                            artifactVersion = "artifactVersion",
                            name = "name",
                            description = "description",
                            website = "website",
                            developers = emptyList(),
                            organization = null,
                            scm = null,
                            licenses = emptySet(),
                            funding = emptySet(),
                            tag = null,
                        )
                    )
                ),
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
        composeTestRule.onNodeWithTag(TestTag.LibrariesContainer).onChildAt(0).performClick()
        //Dialog opens
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertExists()
        //User clicks ok button
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //Dialog closes
        composeTestRule.onNodeWithTag(TestTag.DialogLibrary).assertDoesNotExist()
    }

}