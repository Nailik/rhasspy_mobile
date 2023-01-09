package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.hasTestTag
import org.rhasspy.mobile.android.navigation.BottomBarScreenType
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.viewmodel.screens.ConfigurationScreenViewModel
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
        composeTestRule.onNodeWithTag(BottomBarScreenType.ConfigurationScreen).performClick()
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
        ConfigurationScreenType.values().forEach { tag ->
            composeTestRule.onNodeWithTag(TestTag.List).performScrollToNode(hasTestTag(tag)).assertExists()
            composeTestRule.onNodeWithTag(tag).performClick()
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
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId).performScrollTo()
            .performTextReplacement(textInputTest)
        //text field changed text
        assertEquals(textInputTest, ConfigurationScreenViewModel().siteId.value)
    }

}