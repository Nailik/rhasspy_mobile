package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.viewModels.configuration.DialogManagementConfigurationViewModel
import kotlin.test.assertEquals

class DialogManagementConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = DialogManagementConfigurationViewModel()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                DialogManagementConfigurationContent(viewModel)
            }
        }

    }

    /**
     * option disable is set
     * User clicks option local
     * new option is selected
     *
     * User clicks save
     * option is saved to local
     */
    @Test
    fun testContent() = runBlocking {
        viewModel.selectDialogManagementOption(DialogManagementOptions.Disabled)
        viewModel.save()

        //option disable is set
        composeTestRule.onNodeWithTag(DialogManagementOptions.Disabled, true).onChildAt(0).assertIsSelected()
        //User clicks option local
        composeTestRule.onNodeWithTag(DialogManagementOptions.Local, true).performClick()
        //new option is selected
        composeTestRule.onNodeWithTag(DialogManagementOptions.Local, true).onChildAt(0).assertIsSelected()

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        val newViewModel = DialogManagementConfigurationViewModel()
        //option is saved to local
        assertEquals(DialogManagementOptions.Local, newViewModel.dialogManagementOption.value)
    }

}