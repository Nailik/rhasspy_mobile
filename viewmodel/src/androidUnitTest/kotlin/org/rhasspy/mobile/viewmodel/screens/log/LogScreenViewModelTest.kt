package org.rhasspy.mobile.viewmodel.screens.log

import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.get
import org.rhasspy.mobile.logic.logger.FileLogger
import org.rhasspy.mobile.viewmodel.IKViewModelTest
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.SaveLogFile
import org.rhasspy.mobile.viewmodel.screens.log.LogScreenUiEvent.Action.ShareLogFile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LogScreenViewModelTest : IKViewModelTest() {

    @RelaxedMockK
    lateinit var fileLogger: FileLogger

    private lateinit var logScreenViewModel: LogScreenViewModel

    @BeforeTest
    override fun before() {
        val modulesList = initModules().apply {
            add(
                module {
                    single { fileLogger }
                    single { settings }
                    single { LogScreenViewStateCreator(get()) }
                }
            )
        }
        startKoin {
            modules(modulesList)
        }

        MockKAnnotations.init(this, relaxUnitFun = true)

        super.before()
        logScreenViewModel = LogScreenViewModel(get(), get())
    }

    @AfterTest
    fun after() {
        stopKoin()
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