package org.rhasspy.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Application
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.asImageDesc
import dev.icerock.moko.resources.getImageByFileName
import platform.UIKit.UIViewController
import kotlinx.cinterop.get
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreateCopyWithColorSpace
import platform.CoreGraphics.CGImageGetAlphaInfo
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.UIKit.UIImage

// TODO: Add support for remaining color spaces when the Skia library supports them.
internal fun UIImage.toSkiaImage(): Image? {
    println("toSkiaImage0")
    val imageRef = CGImageCreateCopyWithColorSpace(this.CGImage, CGColorSpaceCreateDeviceRGB()) ?: return null

    println("toSkiaImage1")
    val width = CGImageGetWidth(imageRef).toInt()
    val height = CGImageGetHeight(imageRef).toInt()

    println("toSkiaImage2 $width $height")
    val bytesPerRow = CGImageGetBytesPerRow(imageRef)
    println("toSkiaImage3 $bytesPerRow")
    val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
    println("toSkiaImage4 $data")
    val bytePointer = CFDataGetBytePtr(data)
    println("toSkiaImage5 $bytePointer")
    val length = CFDataGetLength(data)
    val alphaInfo = CGImageGetAlphaInfo(imageRef)

    val alphaType = when (alphaInfo) {
        CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst, CGImageAlphaInfo.kCGImageAlphaPremultipliedLast -> ColorAlphaType.PREMUL
        CGImageAlphaInfo.kCGImageAlphaFirst, CGImageAlphaInfo.kCGImageAlphaLast -> ColorAlphaType.UNPREMUL
        CGImageAlphaInfo.kCGImageAlphaNone, CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst, CGImageAlphaInfo.kCGImageAlphaNoneSkipLast -> ColorAlphaType.OPAQUE
        else -> ColorAlphaType.UNKNOWN
    }
    println("toSkiaImage4")

    val byteArray = ByteArray(length.toInt()) { index ->
        bytePointer!![index].toByte()
    }
    println("toSkiaImage5")
    CFRelease(data)
    CFRelease(imageRef)
    println("toSkiaImage6 $bytesPerRow $data")




    println("toSkiaImage7")
    return Image.makeRaster(
        imageInfo = ImageInfo(width = width, height = height, colorType = ColorType.RGBA_8888, alphaType = alphaType),
        bytes = byteArray,
        rowBytes = bytesPerRow.toInt(),
    )
}

class IosApplication() : Application() {

    init {
        onCreated()
    }

    @Suppress("unused", "FunctionName")
    fun MainViewController(): UIViewController =
        Application("Rhasspy Mobile") { //TODO splash view
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    FilledTonalButton(modifier = Modifier.align(Alignment.Center), onClick = {}) {
                        Text(text = "Hello Compose Ui! - Material3")
                        Text(text =  StringDesc.Resource(MR.strings.backgroundServiceInformation).localized())
                    }

                }
                    androidx.compose.material3.Icon(
                        painter = getPainter(),
                        contentDescription = "text"
                    )
                }
            }

    fun getPainter(): Painter {
        val uiImage = UIImage.imageNamed("ic_launcher")
        println("uiImage $uiImage")
        val skiaImage = uiImage!!.toSkiaImage()
        println("skiaImage $skiaImage")
        return skiaImage!!.toComposeImageBitmap()?.let(::BitmapPainter)!!
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        //TODO call ios
    }

    override fun startOverlay() {
        //TODO??
    }

    override fun stopOverlay() {
        //TODO??
    }

    override suspend fun updateWidget() {
        //TODO??
    }

}