package org.rhasspy.mobile.platformspecific

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))

fun <T1, T2, T3, T4, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T1, T2, T3, T4) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4) { o1, o2, o3, o4 ->
    transform.invoke(o1, o2, o3, o4)
}.stateIn(
    scope,
    sharingStarted,
    transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value)
)

inline fun <reified T, R> combineStateFlow(
    vararg flows: StateFlow<T>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    crossinline transform: (Array<T>) -> R
): StateFlow<R> = combine(flows = flows) {
    transform.invoke(it)
}.stateIn(
    scope = scope,
    started = sharingStarted,
    initialValue = transform.invoke(flows.map {
        it.value
    }.toTypedArray())
)

fun <T1, T2, T3, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T1, T2, T3) -> R
): StateFlow<R> = combine(flow1, flow2, flow3) { o1, o2, o3 ->
    transform.invoke(o1, o2, o3)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value, flow3.value))

fun <T1, T2> combineStateNotEquals(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily
): StateFlow<Boolean> = combine(flow1, flow2) { o1, o2 ->
    o1 != o2
}.stateIn(scope, sharingStarted, flow1.value != flow2.value)

fun combineAny(
    vararg flows: StateFlow<Boolean>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
): StateFlow<Boolean> = combine(*flows) { array: Array<Boolean> ->
    array.contains(true)
}.stateIn(scope, sharingStarted, flows.find { it.value } != null)

fun <T, R> StateFlow<T>.mapReadonlyState(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
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

inline fun <T> Array<out T>.toImmutableList(): ImmutableList<T> {
    return when (size) {
        0 -> persistentListOf()
        1 -> persistentListOf(this[0])
        else -> this.toList().toImmutableList()
    }
}

fun String.toLongOrZero(): Long = toLongOrNull() ?: 0L
fun String.toIntOrZero(): Int = toIntOrNull() ?: 0
fun <E> Iterable<E>.replace(old: E, new: E) = map { if (it == old) new else it }