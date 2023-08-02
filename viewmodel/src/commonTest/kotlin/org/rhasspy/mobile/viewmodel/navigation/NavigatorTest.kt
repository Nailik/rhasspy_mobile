package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.persistentListOf
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.viewmodel.AppTest
import org.rhasspy.mobile.viewmodel.nVerify
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.WebServerConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.ConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screens.home.IHomeScreeViewModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigatorTest : AppTest() {

    private lateinit var navigator: Navigator

    @Mock
    lateinit var homeScreenViewModel: IHomeScreeViewModel

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single { homeScreenViewModel }
            }
        )
        navigator = get<INavigator>() as Navigator
    }

    @Test
    fun `when back stack is popped and there is more than one screen the top screen is closed`() {
        navigator.updateNavStack(persistentListOf(HomeScreen, ConfigurationScreen))
        assertEquals(2, navigator.navStack.value.size)

        navigator.popBackStack()

        //nVerify { repeat(0) { nativeApplication.closeApp() } }
        assertEquals(1, navigator.navStack.value.size)
        assertEquals(HomeScreen, navigator.navStack.value.last())
    }

    @Test
    fun `when user clicks back and top view model handles it popBackStack is not called`() {
        //every { nativeApplication.closeApp() } returns Unit
        every { homeScreenViewModel.onBackPressedClick() } returns true
        navigator.updateNavStack(persistentListOf(HomeScreen))
        navigator.onComposed(homeScreenViewModel)

        navigator.onBackPressed()

        //nVerify { repeat(0) { nativeApplication.closeApp() } }

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

        navigator.replace(MainScreenNavigationDestination::class, ConfigurationScreen)

        assertEquals(ConfigurationScreen, navigator.navStack.value.last())
        assertEquals(1, navigator.navStack.value.size)
    }

    @Test
    fun `when user wants to replace a screen type and it doesn't exists it's added on top of the stack`() {
        navigator.updateNavStack(persistentListOf(HomeScreen))
        assertEquals(1, navigator.navStack.value.size)

        navigator.replace(
            ConfigurationScreenNavigationDestination::class,
            WebServerConfigurationScreen
        )

        assertEquals(WebServerConfigurationScreen, navigator.navStack.value.last())
        assertEquals(2, navigator.navStack.value.size)
    }

    @Test
    fun `when view model is composed it's added to the back stack`() {
        every { homeScreenViewModel.onBackPressedClick() } returns true

        navigator.onComposed(homeScreenViewModel)

        navigator.onBackPressed()
        nVerify { homeScreenViewModel.onBackPressedClick() }
    }

    @Test
    fun `when view model is disposed and exists on the back stack it's removed`() {
        //every { nativeApplication.closeApp() } returns Unit
        navigator.updateNavStack(persistentListOf(ConfigurationScreen, HomeScreen))
        every { homeScreenViewModel.onBackPressedClick() } returns true

        navigator.onComposed(homeScreenViewModel)

        navigator.onDisposed(homeScreenViewModel)

        navigator.onBackPressed()
        nVerify { repeat(0) { homeScreenViewModel.onBackPressedClick() } }
    }
}