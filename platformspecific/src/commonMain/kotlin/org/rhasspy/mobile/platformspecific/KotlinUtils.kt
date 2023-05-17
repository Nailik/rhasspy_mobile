package org.rhasspy.mobile.platformspecific

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))

inline fun <reified T> combineStateFlow(
    vararg flows: StateFlow<T>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily
): StateFlow<Array<T>> = combine(flows = flows) {
    it
}.stateIn(
    scope = scope,
    started = sharingStarted,
    initialValue = flows.map {
        it.value
    }.toTypedArray()
)

fun <T, R> StateFlow<T>.mapReadonlyState(
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T) -> R
): StateFlow<R> = this.map {
    transform(it)
}.stateIn(scope, sharingStarted, transform.invoke(this.value))

val <T> MutableStateFlow<T>.readOnly get(): StateFlow<T> = this

val <T> MutableSharedFlow<T>.readOnly get(): Flow<T> = this

val <T> MutableList<T>.readOnly get(): List<T> = this

inline fun <T1 : Any, T2 : Any> notNull(
    p1: T1?,
    p2: T2?,
    block: (T1, T2) -> Unit,
    run: () -> Unit
) {
    return if (p1 != null && p2 != null) block(p1, p2) else run()
}

fun <T> Array<out T>.toImmutableList(): ImmutableList<T> {
    return when (size) {
        0 -> persistentListOf()
        1 -> persistentListOf(this[0])
        else -> this.toList().toImmutableList()
    }
}

fun String.toLongOrZero(): Long = toLongOrNull() ?: 0L
fun String.toIntOrZero(): Int = toIntOrNull() ?: 0

fun <E> ImmutableList<E>.updateList(block: MutableList<E>.() -> Unit): ImmutableList<E> {
    return this.toMutableList().apply(block).toImmutableList()
}

fun <E> ImmutableList<E>.updateList(index: Int, block: E.() -> E): ImmutableList<E> {
    val item = get(index)
    return this.toImmutableList().updateList {
        set(index, block(item))
    }
}

fun <E> ImmutableList<E>.updateListItem(item: E, block: E.() -> E): ImmutableList<E> {
    val index = indexOf(item)
    return this.toImmutableList().updateList {
        set(index, block(item))
    }
}