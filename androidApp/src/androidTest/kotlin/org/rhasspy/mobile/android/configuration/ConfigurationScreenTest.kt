package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.MainActivity
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.navigation.BottomBarScreenType
import org.rhasspy.mobile.android.utils.hasTestTag
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenViewModel
import kotlin.test.assertEquals

/**
 * Test Configuration Screen
 * Items exist
 * Site ID edit
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConfigurationScreenTest : KoinComponent {

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
    fun testContent() = runTest {
        //SiteId
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId).assertExists()
        //each item exists and navigates
        ConfigurationScreenType.values().forEach { tag ->
            composeTestRule.onNodeWithTag(TestTag.List).performScrollToNode(hasTestTag(tag))
                .assertExists()
            composeTestRule.onNodeWithTag(tag).performClick()
            //content exists
            composeTestRule.onNodeWithTag(tag).assertExists()
            //press toolbar back button
            composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
            composeTestRule.awaitIdle()
        }
    }

    /**
     * Test site id to be changed
     * text field changed text
     * test if change is saved
     */
    @Test
    fun testSiteIdEdit() = runTest {
        val textInputTest = "siteIdTestInput"
        //Test site id to be changed
        composeTestRule.onNodeWithTag(TestTag.ConfigurationSiteId).performScrollTo().performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        //text field changed text
        assertEquals(textInputTest, get<ConfigurationScreenViewModel>().viewState.value.siteId.text)
    }

}