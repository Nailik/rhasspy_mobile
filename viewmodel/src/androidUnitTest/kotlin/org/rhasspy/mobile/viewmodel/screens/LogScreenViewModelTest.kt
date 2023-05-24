package org.rhasspy.mobile.viewmodel.screens

import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewStateCreator
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogScreenViewModelTest : AppTest() {

    @RelaxedMockK
    lateinit var fileLogger: FileLogger

    private lateinit var logScreenViewModel: LogScreenViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { fileLogger }
                single { LogScreenViewStateCreator(get()) }
            }
        )

        logScreenViewModel = LogScreenViewModel(get(), get())
    }

    @Test
    fun `when user requests to save log file it is saved`() {
        logScreenViewModel.onEvent(SaveLogFile)

        coVerify { fileLogger.saveLogFile() }
    }

    @Test
    fun `when user requests to share log file it is shared`() {
        logScreenViewModel.onEvent(ShareLogFile)

        coVerify { fileLogger.shareLogFile() }
    }
}