package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination.HomeScreen

/**
 * top viewmodel
 * wenn screen nicht mehr angezeigt wird disposen
 */
class Navigator(
    private val nativeApplication: NativeApplication
) {

    private val _navStack = MutableStateFlow<ImmutableList<NavigationDestination>>(persistentListOf(HomeScreen))
    val navStack = _navStack.readOnly

    private val _viewModelStack = mutableListOf<KViewModel>()

    inline fun <reified T : NavigationDestination> topScreen(): StateFlow<T?> = navStack.mapReadonlyState { list -> list.filterIsInstance<T>().lastOrNull() }
    inline fun <reified T : NavigationDestination> topScreen(default: T): StateFlow<T> = navStack.mapReadonlyState { list -> list.filterIsInstance<T>().lastOrNull() ?: default }


    fun popBackStack() {
        if (navStack.value.size == 1) {
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
    fun onBackPressed() {
        if (_viewModelStack.lastOrNull()?.onBackPressedClick() != true) {
            //check if top nav destination handles back press
            popBackStack()
        }
    }

    /**
     * navigate to screen (add to backstack)
     */
    fun navigate(screen: NavigationDestination) {
        if (_navStack.value.lastOrNull() != screen) {
            _navStack.update {
                it.updateList {
                    add(screen)
                }
            }
        }
    }

    inline fun <reified T : NavigationDestination> replace(screen: NavigationDestination) {
        val currentStack = navStack.value
        updateNavStack(currentStack.updateList {
            val index = indexOfLast { it is T }
            if (index == -1) {
                add(screen)
            } else {
                set(index, screen)
            }
        })
    }

    fun updateNavStack(list: ImmutableList<NavigationDestination>) {
        _navStack.value = list
    }

    fun onComposed(viewModel: KViewModel) {
        _viewModelStack.add(viewModel)
    }

    fun onDisposed(viewModel: KViewModel) {
        val index = _viewModelStack.indexOfLast { it == viewModel }
        if (index != -1) {
            _viewModelStack.removeAt(index)
        }
    }

}