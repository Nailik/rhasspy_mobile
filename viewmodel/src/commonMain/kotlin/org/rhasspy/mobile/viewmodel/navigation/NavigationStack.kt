package org.rhasspy.mobile.viewmodel.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList

class NavigationStack<T: NavigationDestination>(val initial: T) {

    private val _stack = MutableStateFlow<ImmutableList<T>>(persistentListOf(initial))
    val top = _stack.mapReadonlyState { it.lastOrNull() ?: initial }

    /**
     * returns true if the pop back stack was consumed
     */
    fun popBackStack(): Boolean {
        return if (_stack.value.size == 1) {
            false
        } else {
            _stack.update {
                it.updateList {
                    removeLast()
                }
            }
            true
        }
    }

    /**
     * update main screen
     */
    fun set(screen: T) {
        _stack.update {
            it.updateList {
                clear()
                if (screen != initial) {
                    add(initial)
                }
                add(screen)
            }
        }
    }

    /**
     * navigate to screen (add to backstack)
     */
    fun navigate(screen: T) {
        _stack.update {
            it.updateList {
                add(screen)
            }
        }
    }


}