package org.rhasspy.mobile.platformspecific

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.resume
import kotlin.math.roundToInt

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))


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

inline fun <reified T> combineStateFlow(
    vararg flows: StateFlow<T>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Eagerly
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
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T) -> R
): StateFlow<R> = this.map {
    transform(it)
}.stateIn(scope, sharingStarted, transform.invoke(this.value))

val <T> MutableStateFlow<T>.readOnly get(): StateFlow<T> = this

val <T> MutableSharedFlow<T>.readOnly get(): Flow<T> = this

val <T> MutableList<T>.readOnly get(): List<T> = this

fun Int?.toIntOrZero(): Int = this ?: 0
fun Long?.toLongOrZero(): Long = this ?: 0
fun Int?.toStringOrEmpty(): String = this?.toString() ?: ""
fun Long?.toStringOrEmpty(): String = this?.toString() ?: ""
fun String?.toLongOrNullOrConstant(): Long? =
    this?.replace(" ", "")?.let {
        if (it.length > 10) this.substring(0..9).toLong() else it.trim().trimTrailingZeros()
            ?.toLongOrNull()
    }

fun String?.toIntOrNullOrConstant(): Int? =
    this?.replace(" ", "")?.let {
        if (it.length > 10) this.substring(0..9).toInt() else it.trimTrailingZeros()
            ?.toIntOrNull()
    }

private fun String?.trimTrailingZeros(): String? {
    if (this == null) return null
    val result = this.replaceFirst(Regex("^0*"), "")
    return if (result.isEmpty() && this.isNotEmpty()) {
        return "0"
    } else {
        result
    }
}

fun <E> ImmutableList<E>.updateList(block: MutableList<E>.() -> Unit): ImmutableList<E> {
    return this.toMutableList().apply(block).toImmutableList()
}

fun <E> ImmutableList<E>.updateListItem(item: E, block: E.() -> E): ImmutableList<E> {
    val index = indexOf(item)
    return this.toImmutableList().updateList {
        set(index, block(item))
    }
}

fun Float.roundToDecimals(decimals: Int): Float {
    var dotAt = 1
    repeat(decimals) { dotAt *= 10 }
    val roundedValue = (this * dotAt).roundToInt()
    return (roundedValue / dotAt) + (roundedValue % dotAt).toFloat() / dotAt
}

fun Float.naNToZero(): Float {
    return if (this.isNaN()) {
        return 0F
    } else this
}

fun <T> CancellableContinuation<T>.resumeSave(value: T) {
    if (!this.isCompleted) {
        this.resume(value)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CancellableContinuation<T>.resumeSave(
    value: T,
    onCancellation: ((cause: Throwable) -> Unit)?
) {
    if (!this.isCompleted) {
        this.resume(value, onCancellation)
    }
}