package org.rhasspy.mobile.data.data

fun Int?.toIntOrZero(): Int = this ?: 0
fun Long?.toLongOrZero(): Long = this ?: 0
fun Int?.toStringOrEmpty(): String = this?.toString() ?: ""
fun Long?.toStringOrEmpty(): String = this?.toString() ?: ""
fun String?.toLongOrNullOrConstant(): Long? =
    this?.replace(" ", "")?.let {
        if (it.length > 10) this.substring(0..9).toLong() else it.trim().trimTrailingZeros()
            ?.toLongOrNull()
    }

fun String?.toLongOrZero(): Long =
    this?.replace(" ", "")?.let {
        if (it.length > 10) this.substring(0..9).toLong() else it.trim().trimTrailingZeros()
            ?.toLongOrNull()
    } ?: 0

fun String?.toIntOrZero(): Int =
    this?.replace(" ", "")?.let {
        if (it.length > 9) this.substring(0..9).toInt() else it.trimTrailingZeros()
            ?.toIntOrNull()
    } ?: 0

fun String?.toIntOrNullOrConstant(): Int? =
    this?.replace(" ", "")?.let {
        if (it.length > 9) this.substring(0..9).toInt() else it.trimTrailingZeros()
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