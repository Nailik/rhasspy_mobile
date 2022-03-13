package org.rhasspy.mobile

//https://stackoverflow.com/questions/67179257/how-can-i-convert-an-int-to-a-bytearray-and-then-convert-it-back-to-an-int-with
fun Number.toByteArray(size: Int = 4): ByteArray =
    ByteArray(size) { i -> (this.toLong() shr (i * 8)).toByte() }