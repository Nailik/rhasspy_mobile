package org.rhasspy.mobile.viewmodel.screens

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.logger.IFileLogger
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.coVerify
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogScreenViewModelTest : AppTest() {

    @Mock
    lateinit var fileLogger: IFileLogger

    private lateinit var logScreenViewModel: LogScreenViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { fileLogger }
            }
        )

        logScreenViewModel = get()
    }

    @Test
    fun `when user requests to save log file it is saved`() = runTest {
        logScreenViewModel.onEvent(SaveLogFile)

        coVerify { fileLogger.saveLogFile() }
    }

    @Test
    fun `when user requests to share log file it is shared`() = runTest {
        logScreenViewModel.onEvent(ShareLogFile)

        coVerify { fileLogger.shareLogFile() }
    }
}