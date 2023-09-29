package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.saveBottomAppBar
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.ui.configuration.DialogManagementConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationUiEvent.Change.SelectDialogManagementOption
import org.rhasspy.mobile.viewmodel.configuration.dialogmanagement.DialogManagementConfigurationViewModel
import kotlin.test.assertEquals

class DialogManagementConfigurationContentTest : FlakyTest() {

    private val viewModel = get<DialogManagementConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        DialogManagementConfigurationScreen(viewModel)
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
    @AllowFlaky
    fun testEndpoint() = runTest {
        setupContent()

        viewModel.onEvent(SelectDialogManagementOption(DialogManagementOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(DialogManagementOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()
        //User clicks option local
        composeTestRule.onNodeWithTag(DialogManagementOption.Local).performClick()
        composeTestRule.awaitIdle()
        //new option is selected
        composeTestRule.onNodeWithTag(DialogManagementOption.Local, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks save
        composeTestRule.saveBottomAppBar()
        DialogManagementConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to local
            assertEquals(DialogManagementOption.Local, it.dialogManagementOption)
        }
    }

}