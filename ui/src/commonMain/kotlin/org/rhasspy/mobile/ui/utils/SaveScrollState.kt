package org.rhasspy.mobile.ui.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Static field, contains all scroll values
 */
private val SaveMapScrollState = mutableMapOf<ListType, ScrollKeyParams>()
private val SaveMapLazyListState = mutableMapOf<ListType, KeyParams>()

private data class KeyParams(
    val index: Int,
    val scrollOffset: Int
)

private data class ScrollKeyParams(
    val value: Int
)

/**
 * Save scroll state on all time.
 * @param key value for comparing screen
 * @param initial see [ScrollState.value]
 */
@Composable
fun rememberForeverScrollState(
    key: ListType,
    initial: Int = 0
): ScrollState {
    val scrollState = rememberSaveable(saver = ScrollState.Saver) {
        val scrollValue: Int = SaveMapScrollState[key]?.value ?: initial
        SaveMapScrollState[key] = ScrollKeyParams(scrollValue)
        return@rememberSaveable ScrollState(scrollValue)
    }
    DisposableEffect(Unit) {
        onDispose {
            SaveMapScrollState[key] = ScrollKeyParams(scrollState.value)
        }
    }
    return scrollState
}

@Composable
fun rememberForeverLazyListState(
    key: ListType,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        val savedValue = SaveMapLazyListState[key]
        val savedIndex = savedValue?.index ?: initialFirstVisibleItemIndex
        val savedOffset = savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        LazyListState(
            savedIndex,
            savedOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            val lastIndex = scrollState.firstVisibleItemIndex
            val lastOffset = scrollState.firstVisibleItemScrollOffset
            SaveMapLazyListState[key] = KeyParams(lastIndex, lastOffset)
        }
    }
    return scrollState
}