package org.rhasspy.mobile.android.settings

import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.main.SettingsScreen
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTestNew
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.ui.LocalSnackBarHostState
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.theme.AppTheme
import org.rhasspy.mobile.viewmodel.ViewModelFactory
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination

/**
 * Test Settings Screen
 * Items exist
 * Site ID edit
 */
class SettingsScreenTest : FlakyTestNew() {

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
        initComposable()

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

    private fun initComposable() {
        scenario.onActivity { activity ->
            activity.setContent {
                AppTheme {
                    val snackBarHostState = remember { SnackbarHostState() }

                    CompositionLocalProvider(
                        LocalSnackBarHostState provides snackBarHostState,
                        LocalViewModelFactory provides get<ViewModelFactory>()
                    ) {
                        SettingsScreen()
                    }
                }

            }
        }
    }

}