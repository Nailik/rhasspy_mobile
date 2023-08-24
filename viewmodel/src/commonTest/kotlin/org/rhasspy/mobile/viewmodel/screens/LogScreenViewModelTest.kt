package org.rhasspy.mobile.viewmodel.screens

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.logger.IDatabaseLogger
import org.rhasspy.mobile.testutils.AppTest
import org.rhasspy.mobile.testutils.coEvery
import org.rhasspy.mobile.testutils.coVerify
import org.rhasspy.mobile.testutils.nVerify
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogScreenViewModelTest : AppTest() {

    @Mock
    lateinit var fileLogger: IDatabaseLogger

    private lateinit var logScreenViewModel: LogScreenViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { fileLogger }
            }
        )
    }

    @Test
    fun `when user requests to save log file it is saved`() = runTest {
        every { fileLogger.flow } returns MutableSharedFlow()
        coEvery { fileLogger.saveLogFile() } returns true

        logScreenViewModel = get()
        logScreenViewModel.onEvent(SaveLogFile)

        coVerify { fileLogger.saveLogFile() }
    }

    @Test
    fun `when user requests to share log file it is shared`() {
        every { fileLogger.flow } returns MutableSharedFlow()
        every { fileLogger.shareLogFile() } returns true

        logScreenViewModel = get()
        logScreenViewModel.onEvent(ShareLogFile)

        nVerify { fileLogger.shareLogFile() }
    }
}