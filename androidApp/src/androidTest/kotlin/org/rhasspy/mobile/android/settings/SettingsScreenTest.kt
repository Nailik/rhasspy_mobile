package org.rhasspy.mobile.android.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main.SettingsScreen
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.rhasspy.mobile.android.utils.FlakyTestNew
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination

/**
 * Test Settings Screen
 * Items exist
 * Site ID edit
 */
class SettingsScreenTest : FlakyTestNew() {

    @Composable
    override fun ComposableContent() {
        SettingsScreen()
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
    @AllowFlaky
    fun testContent() = runTest {
        //each item exists and navigates
        SettingsScreenDestination.values().filter { it != SettingsScreenDestination.OverviewScreen }
            .forEach { tag ->
                composeTestRule.awaitIdle()
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