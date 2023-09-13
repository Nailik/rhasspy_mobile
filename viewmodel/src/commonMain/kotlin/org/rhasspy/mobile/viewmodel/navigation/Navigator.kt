package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.toast.longToast
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination.HomeScreen
import kotlin.reflect.KClass

interface INavigator {

    val navStack: StateFlow<ImmutableList<NavigationDestination>>
    val topScreen: StateFlow<NavigationDestination>
    fun popBackStack()
    fun onBackPressed()
    fun navigate(screen: NavigationDestination)
    fun navigate(vararg screen: NavigationDestination)
    fun replace(clazz: KClass<out NavigationDestination>, screen: NavigationDestination)
}

/**
 * top viewmodel
 * wenn screen nicht mehr angezeigt wird disposen
 */
internal class Navigator(
    private val nativeApplication: NativeApplication
) : INavigator {

    override val navStack = MutableStateFlow<ImmutableList<NavigationDestination>>(persistentListOf(HomeScreen))
    override val topScreen: StateFlow<NavigationDestination> = navStack.mapReadonlyState { list -> list.lastOrNull() ?: HomeScreen }

    private var confirmedClose = false

    override fun popBackStack() {
        if (navStack.value.size <= 1) {
            if (confirmedClose) {
                confirmedClose = false
                nativeApplication.closeApp()
            } else {
                confirmedClose = true
                nativeApplication.longToast(MR.strings.closeAppInformation)
            }
        } else {
            navStack.update {
                it.updateList {
                    removeLast().viewModel.onDismissed()
                }
            }
        }
    }

    /**
     * go to previous screen
     */
    override fun onBackPressed() {
        if (!topScreen.value.viewModel.onBackPressedClick()) {
            //check if top nav destination handles back press
            popBackStack()
        }
    }

    /**
     * navigate to screen (add to backstack)
     */
    override fun navigate(screen: NavigationDestination) {
        navStack.update {
            it.updateList {
                add(screen)
            }
        }
    }

    override fun navigate(vararg screen: NavigationDestination) {
        navStack.update {
            it.updateList {
                addAll(screen)
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

    fun updateNavStack(list: ImmutableList<NavigationDestination>) {
        navStack.value = list
    }

}