package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.BottomBarScreens
import org.rhasspy.mobile.android.onNodeWithTag


@RunWith(AndroidJUnit4::class)
class ConfigurationScreenTest {

    //activity necessary for permission
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

}