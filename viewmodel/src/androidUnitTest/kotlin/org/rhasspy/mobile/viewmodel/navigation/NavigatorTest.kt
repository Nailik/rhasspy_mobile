package org.rhasspy.mobile.viewmodel.navigation

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination.WebServerConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.ConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigatorTest : AppTest() {

    private lateinit var navigator: Navigator

    @RelaxedMockK
    lateinit var nativeApplication: NativeApplication

    @RelaxedMockK
    lateinit var homeScreenViewModel: HomeScreenViewModel

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { nativeApplication }
                single { homeScreenViewModel }
            }
        )
        navigator = get()
    }

    @Test
    fun `when back stack is popped and there is only one screen the app is closed`() {
        navigator.updateNavStack(persistentListOf(HomeScreen))
        assertEquals(1, navigator.navStack.value.size)

        navigator.popBackStack()

        verify { nativeApplication.closeApp() }

        assertEquals(1, navigator.navStack.value.size)
    }

    @Test
    fun `when back stack is popped and there is more than one screen the top screen is closed`() {
        navigator.updateNavStack(persistentListOf(HomeScreen, ConfigurationScreen))
        assertEquals(2, navigator.navStack.value.size)

        navigator.popBackStack()

        verify(exactly = 0) { nativeApplication.closeApp() }
        assertEquals(1, navigator.navStack.value.size)
        assertEquals(HomeScreen, navigator.navStack.value.last())
    }

    @Test
    fun `when user clicks back and top view model doesn't handle it popBackStack is called`() {
        every { homeScreenViewModel.onBackPressedClick() } returns false
        navigator.updateNavStack(persistentListOf(HomeScreen))
        navigator.onComposed(homeScreenViewModel)

        navigator.onBackPressed()

        verify { nativeApplication.closeApp() }

        assertEquals(1, navigator.navStack.value.size)
    }

    @Test
    fun `when user clicks back and top view model handles it popBackStack is not called`() {
        every { homeScreenViewModel.onBackPressedClick() } returns false
        every { homeScreenViewModel.onBackPressedClick() } returns true
        navigator.updateNavStack(persistentListOf(HomeScreen))
        navigator.onComposed(homeScreenViewModel)

        navigator.onBackPressed()

        verify(exactly = 0) { nativeApplication.closeApp() }

        assertEquals(1, navigator.navStack.value.size)
    }

    @Test
    fun `when user wants to navigate to a screen it's added to the backstack if it is not the most recent screen`() {
        assertEquals(1, navigator.navStack.value.size)

        navigator.navigate(ConfigurationScreen)

        assertEquals(ConfigurationScreen, navigator.navStack.value.last())
        assertEquals(2, navigator.navStack.value.size)

        navigator.navigate(ConfigurationScreen)

        assertEquals(ConfigurationScreen, navigator.navStack.value.last())
        assertEquals(2, navigator.navStack.value.size)
    }

    @Test
    fun `when user wants to replace a screen type and it exists the top most screen of this type is replaced`() {
        navigator.updateNavStack(persistentListOf(HomeScreen))
        assertEquals(1, navigator.navStack.value.size)

        navigator.replace<MainScreenNavigationDestination>(ConfigurationScreen)

        assertEquals(ConfigurationScreen, navigator.navStack.value.last())
        assertEquals(1, navigator.navStack.value.size)
    }

    @Test
    fun `when user wants to replace a screen type and it doesn't exists it's added on top of the stack`() {
        navigator.updateNavStack(persistentListOf(HomeScreen))
        assertEquals(1, navigator.navStack.value.size)

        navigator.replace<ConfigurationScreenNavigationDestination>(WebServerConfigurationScreen)

        assertEquals(WebServerConfigurationScreen, navigator.navStack.value.last())
        assertEquals(2, navigator.navStack.value.size)
    }

    @Test
    fun `when view model is composed it's added to the back stack`() {
        navigator.onComposed(homeScreenViewModel)

        navigator.onBackPressed()
        verify { homeScreenViewModel.onBackPressedClick() }
    }

    @Test
    fun `when view model is disposed and exists on the back stack it's removed`() {
        navigator.onComposed(homeScreenViewModel)

        navigator.onDisposed(homeScreenViewModel)

        navigator.onBackPressed()
        verify(exactly = 0) { homeScreenViewModel.onBackPressedClick() }
    }
}