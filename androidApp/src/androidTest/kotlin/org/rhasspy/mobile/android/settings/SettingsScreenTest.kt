package org.rhasspy.mobile.android.settings

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.navigation.BottomBarScreenType
import org.rhasspy.mobile.android.onNodeWithTag

/**
 * Test Settings Screen
 * Items exist
 * Site ID edit
 */
class SettingsScreenTest {

    @get: Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        //open configuration screen
        composeTestRule.onNodeWithTag(BottomBarScreenType.SettingsScreen).performClick()
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
        //each item exists and navigates
        SettingsScreenType.values().forEach { tag ->
            composeTestRule.onNodeWithTag(tag).performScrollTo().performClick()
            //content exists
            composeTestRule.onNodeWithTag(tag).assertExists()
            //press toolbar back button
            composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        }
    }

}