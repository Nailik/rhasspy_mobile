package org.rhasspy.mobile.viewmodel.configuration.dialogmanagement

import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DialogActionStateActionStateManagementConfigurationViewModelTest : AppTest() {

    @Mock
    lateinit var nativeApplication: INativeApplication
    override fun setUpMocks() = injectMocks(mocker)

    private lateinit var dialogManagementConfigurationViewModel: DialogManagementConfigurationViewModel

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