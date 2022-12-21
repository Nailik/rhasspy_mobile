package org.rhasspy.mobile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

//https://stackoverflow.com/questions/67179257/how-can-i-convert-an-int-to-a-bytearray-and-then-convert-it-back-to-an-int-with
fun Number.toByteArray(size: Int = 4): ByteArray =
    ByteArray(size) { i -> (this.toLong() shr (i * 8)).toByte() }

//https://stackoverflow.com/questions/19145213/android-audio-capture-silence-detection
//check if any byte is above the threshold
fun List<Byte>.isNotAboveThreshold(threshold: Float): Boolean {
    return find { it >= threshold || it <= -threshold } == null
}

//https://stackoverflow.com/questions/13039846/what-do-the-bytes-in-a-wav-file-represent
//adds wav header in front of data
fun MutableList<Byte>.addWavHeader(): List<Byte> {
    val dataSize = (this.size + 44 - 8).toByteArray()
    val audioDataSize = this.size.toByteArray()

    val header = byteArrayOf(
        82,
        73,
        70,
        70,
        dataSize[0],
        dataSize[1],
        dataSize[2],
        dataSize[3], //4-7 overall size
        87,
        65,
        86,
        69,
        102,
        109,
        116,
        32,
        16,
        0,
        0,
        0,
        1,
        0,
        1,
        0,
        -128,
        62,
        0,
        0,
        0,
        125,
        0,
        0,
        2,
        0,
        16,
        0,
        100,
        97,
        116,
        97,
        audioDataSize[0],
        audioDataSize[1],
        audioDataSize[2],
        audioDataSize[3] //40-43 data size of rest
    )
    this.addAll(0, header.toList())
    return this
}

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

fun <T1, T2, T3, T4, T5, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    sharingStarted: SharingStarted = SharingStarted.Lazily,
    transform: (T1, T2, T3, T4, T5) -> R
): StateFlow<R> = combine(flow1, flow2, flow3, flow4, flow5) { o1, o2, o3, o4, o5 ->
    transform.invoke(o1, o2, o3, o4, o5)
}.stateIn(
    scope,
    sharingStarted,
    transform.invoke(flow1.value, flow2.value, flow3.value, flow4.value, flow5.value)
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

val <T> MutableSharedFlow<T>.readOnly get(): SharedFlow<T> = this