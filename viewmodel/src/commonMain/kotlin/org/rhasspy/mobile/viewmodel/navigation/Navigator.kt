package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList

class Navigator {

    private val _backStack = MutableStateFlow<ImmutableList<Screen>>(persistentListOf(Screen.HomeScreen))
    val backStack = _backStack.readOnly

    /**
     * go to previous screen
     */
    fun popBackStack() {
        if (_backStack.value.size == 1) {
            //TODO close app
        } else {
            _backStack.update {
                it.updateList {
                    removeLast()
                }
            }
        }
    }

    /**
     * update main screen
     */
    fun set(screen: Screen) {
        _backStack.update {
            it.updateList {
                clear()
                if(screen != Screen.HomeScreen) {
                    add(Screen.HomeScreen)
                }
                add(screen)
            }
        }
    }

    /**
     * navigate to screen (add to backstack)
     */
    fun navigate(screen: Screen) {
        _backStack.update {
            it.updateList {
                add(screen)
            }
        }
    }

}