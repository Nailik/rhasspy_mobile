package org.rhasspy.mobile.viewmodel.configuration

import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import kotlin.test.BeforeTest

class IConfigurationEditViewModelTest : AppTest() {

    // private lateinit var iConfigurationViewModelTest: IConfigurationEditViewModelTest

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {

            }
        )

        // iConfigurationViewModelTest = get()
    }

/*
    @Test
    fun getContentViewState() {
    }

    @Test
    fun getData() {
    }

    @Test
    fun getViewState() {
    }

    @Test
    fun updateViewState() {
    }

    @Test
    fun onAction() {
    }

    @Test
    fun getTestScope() {
    }

    @Test
    fun onDiscard() {
    }

    @Test
    fun onSave() {
    }

    @Test
    fun onBackPressed() {
    }

 */
}