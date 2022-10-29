package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.BottomBarScreens
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.viewModels.ConfigurationScreenViewModel
import kotlin.test.assertEquals

/**
 * Test Configuration Screen
 * Items exist
 * Site ID edit
 */
class ConfigurationScreenTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        //open configuration screen
        composeTestRule.onNodeWithTag(BottomBarScreens.ConfigurationScreen).performClick()
    }

    /**
     * Tests that content exists
     *
     * SiteId
     * ConfigurationScreens::values
     * navigate, content exists
     * back button exists
     */
    @Test
    fun testContent() = runBlocking {
        //SiteId
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId).assertExists()
        //each item exists and navigates
        ConfigurationScreens.values().forEach { tag ->
            //navigate
            composeTestRule.onNodeWithTag(tag).performScrollTo().performClick()
            //content exists
            composeTestRule.onNodeWithTag(tag).assertExists()
            //press toolbar back button
            composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        }
    }

    /**
     * Test site id to be changed
     * text field changed text
     * test if change is saved
     */
    @Test
    fun testSiteIdEdit() {
        val textInputTest = "siteIdTestInput"
        //Test site id to be changed
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId, true).performScrollTo().onChild().performTextClearance()
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId, true).performScrollTo().onChild().performTextInput(textInputTest)
        //text field changed text
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId, true).onChild().assertTextEquals(textInputTest)
        assertEquals(textInputTest, ConfigurationScreenViewModel().siteId.value)
    }

}