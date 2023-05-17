package org.rhasspy.mobile.android.settings

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.SettingsScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination

/**
 * Test Settings Screen
 * Items exist
 * Site ID edit
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenTest {

    @get: Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        //open configuration screen
        composeTestRule.onNodeWithTag(SettingsScreen).performClick()
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
    fun testContent() = runTest {
        //each item exists and navigates
        SettingsScreenDestination.values().forEach { tag ->
            composeTestRule.onNodeWithTag(TestTag.List).performScrollToNode(hasTestTag(tag))
                .assertExists()
            composeTestRule.onNodeWithTag(tag).performClick()
            //content exists
            composeTestRule.onNodeWithTag(tag).assertExists()
            //press toolbar back button
            composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        }
    }

}