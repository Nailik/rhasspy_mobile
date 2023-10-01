package org.rhasspy.mobile.platformspecific

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import kotlin.coroutines.resume
import kotlin.math.roundToInt
import kotlin.time.Duration

@OptIn(FlowPreview::class)
fun <T> Flow<T>.timeoutWithDefault(
    timeout: Duration,
    default: T,
): Flow<T> =
    timeout(timeout).catch { exception ->
        if (exception is TimeoutCancellationException) {
            // Catch the TimeoutCancellationException emitted above.
            // Emit desired item on timeout.
            emit(default)
        } else {
            // Throw other exceptions.
            throw exception
        }
    }

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))


fun <T1, T2, T3, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3) -> R
): StateFlow<R> = combine(flow1, flow2, flow3) { o1, o2, o3 ->
    transform.invoke(o1, o2, o3)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value, flow3.value))

fun <T1, T2, T3, T4, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3, T4) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4) { o1, o2, o3, o4 ->
    transform.invoke(o1, o2, o3, o4)
}.stateIn(
    scope,
    sharingStarted,
    transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value)
)

fun <T1, T2, T3, T4, T5, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2, T3, T4, T5) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4, flow5) { o1, o2, o3, o4, o5 ->
    transform.invoke(o1, o2, o3, o4, o5)
}.stateIn(
    scope,
    sharingStarted,
    transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value, flow5.value)
)

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

fun <T> Flow<T>.simpleStateIn(
    initial: T,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
): StateFlow<T> = this.stateIn(scope, sharingStarted, initialValue = initial)

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
        0    -> persistentListOf()
        1    -> persistentListOf(this[0])
        else -> this.toList().toImmutableList()
    }
}

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
fun <T> CancellableContinuation<T>.resumeSave(value: T, onCancellation: ((cause: Throwable) -> Unit)?) {
    if (!this.isCompleted) {
        this.resume(value, onCancellation)
    }
}

fun Boolean.toText(): StableStringResource {
    return if (this) MR.strings.enabled.stable else MR.strings.disabled.stable
}
