package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination.HomeScreen
import kotlin.reflect.KClass

class Navigator {

    private val _backStack = MutableStateFlow<ImmutableList<NavigationStack<*>>>(persistentListOf(NavigationStack(HomeScreen)))
    val backStack = _backStack.readOnly

    fun <T: NavigationDestination> getBackStack(type: KClass<T>, initialScreen: T): NavigationStack<T> {
        @Suppress("UNCHECKED_CAST")
        val navigationStack: NavigationStack<T>? = _backStack.value.firstOrNull { it.initial::class == type } as NavigationStack<T>?

        return if(navigationStack == null){
            val newNavigationStack = NavigationStack(initialScreen)
            _backStack.update {
                it.updateList {
                    add(newNavigationStack)
                }
            }
            newNavigationStack
        } else {
            navigationStack
        }
    }

    /**
     * go to previous screen
     */
    fun popBackStack() {
        val backStackList = _backStack.value.toMutableList()
        var topNavigationStack = backStackList.lastOrNull()

        if (topNavigationStack == null) {
            //TODO close app
        } else {
            while(topNavigationStack?.popBackStack() == false) {
                backStackList.removeLast()
                topNavigationStack = backStackList.lastOrNull()
            }
        }

        _backStack.value = backStackList.toImmutableList()
    }

    /**
     * update main screen
     */
    fun <T: NavigationDestination> set(type: KClass<T>, screen: T) {
        @Suppress("UNCHECKED_CAST")
        val navigationStack: NavigationStack<T>? = _backStack.value.firstOrNull { it.initial::class == type } as NavigationStack<T>?

        if(navigationStack == null){
            val newNavigationStack = NavigationStack(screen)
            _backStack.update {
                it.updateList {
                    add(newNavigationStack)
                }
            }
        } else {
            navigationStack.set(screen)
        }
    }

    /**
     * navigate to screen (add to backstack)
     */
    fun <T: NavigationDestination> navigate(type: KClass<T>, screen: T) {
        @Suppress("UNCHECKED_CAST")
        val navigationStack: NavigationStack<T>? = _backStack.value.firstOrNull { it.initial::class == type } as NavigationStack<T>?

        if(navigationStack == null){
            val newNavigationStack = NavigationStack(screen)
            _backStack.update {
                it.updateList {
                    add(newNavigationStack)
                }
            }
        } else {
            navigationStack.navigate(screen)
        }
    }

}