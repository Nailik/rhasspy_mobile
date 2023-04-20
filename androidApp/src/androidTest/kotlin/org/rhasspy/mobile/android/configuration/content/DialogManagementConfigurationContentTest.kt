package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.utils.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.SelectDialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DialogManagementConfigurationContentTest : KoinComponent {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = get<DialogManagementConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                DialogManagementConfigurationContent()
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
    fun testEndpoint() = runTest {
        viewModel.onEvent(SelectDialogManagementOption(DialogManagementOption.Disabled))
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(DialogManagementOption.Disabled, true).onListItemRadioButton().assertIsSelected()
        //User clicks option local
        composeTestRule.onNodeWithTag(DialogManagementOption.Local).performClick()
        //new option is selected
        composeTestRule.onNodeWithTag(DialogManagementOption.Local, true).onListItemRadioButton().assertIsSelected()

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        DialogManagementConfigurationViewModel(get()).viewState.value.editViewState.value.also {
            //option is saved to local
            assertEquals(DialogManagementOption.Local, it.dialogManagementOption)
        }
    }

}