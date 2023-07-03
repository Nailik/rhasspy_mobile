package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.application.INativeApplication
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel
import kotlin.reflect.KClass

interface INavigator {

    val navStack: StateFlow<ImmutableList<NavigationDestination>>
    fun popBackStack()
    fun onBackPressed()
    fun navigate(screen: NavigationDestination)
    fun onComposed(viewModel: IScreenViewModel)
    fun onDisposed(viewModel: IScreenViewModel)
    fun replace(clazz: KClass<out NavigationDestination>, screen: NavigationDestination)
}

inline fun <reified T : NavigationDestination> INavigator.topScreen(): StateFlow<T?> = navStack.mapReadonlyState { list -> list.filterIsInstance<T>().lastOrNull() }
inline fun <reified T : NavigationDestination> INavigator.topScreen(default: T): StateFlow<T> = navStack.mapReadonlyState { list -> list.filterIsInstance<T>().lastOrNull() ?: default }

/**
 * top viewmodel
 * wenn screen nicht mehr angezeigt wird disposen
 */
internal class Navigator(
    private val nativeApplication: INativeApplication
) : INavigator {

    private val _navStack = MutableStateFlow<ImmutableList<NavigationDestination>>(persistentListOf(HomeScreen))
    override val navStack = _navStack.readOnly

    private val _viewModelStack = mutableListOf<IScreenViewModel>()

    override fun popBackStack() {
        if (navStack.value.size <= 1) {
            nativeApplication.closeApp()
        } else {
            _navStack.update {
                it.updateList {
                    removeLast()
                }
            }
        }
    }

    /**
     * go to previous screen
     */
    override fun onBackPressed() {
        if (_viewModelStack.lastOrNull()?.onBackPressedClick() != true) {
            //check if top nav destination handles back press
            popBackStack()
        }
    }

    /**
     * navigate to screen (add to backstack)
     */
    override fun navigate(screen: NavigationDestination) {
        if (_navStack.value.lastOrNull() != screen) {
            _navStack.update {
                it.updateList {
                    add(screen)
                }
            }
        }
    }

    override fun replace(clazz: KClass<out NavigationDestination>, screen: NavigationDestination) {
        val currentStack = navStack.value
        val list = currentStack.updateList {
            val index = indexOfLast { clazz.isInstance(it) }
            if (index == -1) {
                add(screen)
            } else {
                set(index, screen)
            }
        }
        updateNavStack(list)
    }

    internal fun updateNavStack(list: ImmutableList<NavigationDestination>) {
        _navStack.value = list
    }

    override fun onComposed(viewModel: IScreenViewModel) {
        _viewModelStack.add(viewModel)
    }

    override fun onDisposed(viewModel: IScreenViewModel) {
        val index = _viewModelStack.indexOfLast { it == viewModel }
        if (index != -1) {
            _viewModelStack.removeAt(index)
        }
    }

}