package org.rhasspy.mobile.viewmodel.screens

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.generated.injectMocks
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.logger.IFileLogger
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.coEvery
import org.rhasspy.mobile.viewmodel.coVerify
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogScreenViewModelTest : AppTest() {

    @Mock
    lateinit var fileLogger: IFileLogger

    private lateinit var logScreenViewModel: LogScreenViewModel

    override fun setUpMocks() = mocker.injectMocks(this)

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
        every { fileLogger.getLines() } returns persistentListOf()
        coEvery { fileLogger.saveLogFile() } returns true

        logScreenViewModel = get()
        logScreenViewModel.onEvent(SaveLogFile)

        coVerify { fileLogger.saveLogFile() }
    }

    @Test
    fun `when user requests to share log file it is shared`() {
        every { fileLogger.flow } returns MutableSharedFlow()
        every { fileLogger.getLines() } returns persistentListOf()
        every { fileLogger.shareLogFile() } returns true

        logScreenViewModel = get()
        logScreenViewModel.onEvent(ShareLogFile)

        nVerify { fileLogger.shareLogFile() }
    }
}