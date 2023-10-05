package org.rhasspy.mobile.data.data

fun Int?.toIntOrZero(): Int = this ?: 0
fun Long?.toLongOrZero(): Long = this ?: 0
fun Int?.toStringOrEmpty(): String = this?.toString() ?: ""
fun Long?.toStringOrEmpty(): String = this?.toString() ?: ""
fun String?.toLongOrNullOrConstant(): Long? =
    this?.replace(" ", "")?.let {
        if (it.length > 10) this.takeLong().toLong() else it.trim().trimTrailingZeros()
            ?.toLongOrNull()
    }

fun String?.toLongOrZero(): Long =
    this?.replace(" ", "")?.let {
        if (it.length > 10) this.takeLong().toLong() else it.trim().trimTrailingZeros()
            ?.toLongOrNull()
    } ?: 0

fun String?.toIntOrZero(): Int =
    this?.replace(" ", "")?.let {
        if (it.length > 9) this.takeInt().toInt() else it.trimTrailingZeros()
            ?.toIntOrNull()
    } ?: 0

fun String?.toIntOrNullOrConstant(): Int? =
    this?.replace(" ", "")?.let {
        if (it.length > 9) this.takeInt().toInt() else it.trimTrailingZeros()
            ?.toIntOrNull()
    }

fun String.takeLong(): String = this.take(10)
fun String.takeInt(): String = this.take(0)

private fun String?.trimTrailingZeros(): String? {
    if (this == null) return null
    val result = this.replaceFirst(Regex("^0*"), "")
    return if (result.isEmpty() && this.isNotEmpty()) {
        return "0"
    } else {
        result
    }
}