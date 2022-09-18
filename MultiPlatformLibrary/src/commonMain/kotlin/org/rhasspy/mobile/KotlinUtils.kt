package org.rhasspy.mobile

//https://stackoverflow.com/questions/67179257/how-can-i-convert-an-int-to-a-bytearray-and-then-convert-it-back-to-an-int-with
fun Number.toByteArray(size: Int = 4): ByteArray =
    ByteArray(size) { i -> (this.toLong() shr (i * 8)).toByte() }

//https://stackoverflow.com/questions/19145213/android-audio-capture-silence-detection
//check if any byte is above the threshold
fun List<Byte>.isNotAboveThreshold(threshold: Int): Boolean {
    return find { it >= threshold || it <= -threshold } == null
}

//https://stackoverflow.com/questions/13039846/what-do-the-bytes-in-a-wav-file-represent
//adds wav header in front of data
fun MutableList<Byte>.addWavHeader() {
    val dataSize = (this.size + 44 - 8).toByteArray()
    val audioDataSize = this.size.toByteArray()

    val header = byteArrayOf(
        82, 73, 70, 70,
        dataSize[0], dataSize[1], dataSize[2], dataSize[3], //4-7 overall size
        87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, 1, 0, -128, 62, 0, 0, 0, 125, 0, 0, 2, 0, 16, 0, 100, 97, 116, 97,
        audioDataSize[0], audioDataSize[1], audioDataSize[2], audioDataSize[3] //40-43 data size of rest
    )
    this.addAll(0, header.toList())
}