package org.rhasspy.mobile.android.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.main.MainScreen
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.SettingsScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination
import org.rhasspy.mobile.viewmodel.screens.main.MainScreenViewModel

/**
 * Test Settings Screen
 * Items exist
 * Site ID edit
 */
class SettingsScreenTest : FlakyTest() {

    private val viewModel = get<MainScreenViewModel>()

    @Composable
    override fun ComposableContent() {
        MainScreen(viewModel)
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
        get<INavigator>().navigate(SettingsScreen)
        setupContent()

        //each item exists and navigates
        SettingsScreenDestination::class.sealedSubclasses.forEach { tag ->
            composeTestRule.awaitIdle()
            composeTestRule.onNodeWithTag(TestTag.List).performScrollToNode(hasTestTag(tag as NavigationDestination)).assertExists()
            composeTestRule.onNodeWithTag(tag as NavigationDestination).performClick()
            //content exists
            composeTestRule.awaitIdle()
            composeTestRule.onNodeWithTag(tag as NavigationDestination).assertExists()
            //press toolbar back button
            composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
        }
    }


}