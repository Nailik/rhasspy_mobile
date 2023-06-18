package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.configuration.edit.dialogmanagement.DialogManagementConfigurationEditViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class DialogActionStateActionStateManagementConfigurationViewModelTest : AppTest() {

    private lateinit var dialogManagementConfigurationViewModel: DialogManagementConfigurationEditViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        dialogManagementConfigurationViewModel = get()
    }

    @Test
    fun getScreen() {
    }

    @Test
    fun onEvent() {
    }

    @Test
    fun onChange() {
    }

    @Test
    fun onAction() {
    }

    @Test
    fun onDiscard() {
    }

    @Test
    fun onSave() {
    }
}