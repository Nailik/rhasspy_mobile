package org.rhasspy.mobile.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


class TestKViewModel : KViewModel() {

}


class KViewModelTest : KoinTest {

    @RelaxedMockK
    lateinit var navigator: Navigator

    @RelaxedMockK
    lateinit var nativeApplication: NativeApplication

    private val testKViewModel = TestKViewModel()

    @BeforeTest
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        startKoin {
            modules(
                module {
                    single { nativeApplication }
                    single { navigator }
                }
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun getNavigator() {
    }

    @Test
    fun getKViewState() {
    }

    @Test
    fun composed() {
        testKViewModel.composed()
        verify(exactly = 1) { navigator.onComposed(testKViewModel) }
    }

    @Test
    fun disposed() {
        testKViewModel.disposed()
        verify(exactly = 1) { navigator.onDisposed(testKViewModel) }
    }

    @Test
    fun requireMicrophonePermission() {
    }

    @Test
    fun onEvent() {
    }

    @Test
    fun onBackPressed() {
    }

    @Test
    fun onBackPressedClick() {
    }
}