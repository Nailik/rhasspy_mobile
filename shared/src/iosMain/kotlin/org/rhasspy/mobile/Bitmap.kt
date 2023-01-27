package org.rhasspy.mobile

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.base64EncodedStringWithOptions
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

 class Bitmap(val image: UIImage) {

     fun toByteArray(): ByteArray {
        val imageData = UIImageJPEGRepresentation(image, 0.99)
            ?: throw IllegalArgumentException("image data is null")
        val bytes = imageData.bytes ?: throw IllegalArgumentException("image bytes is null")
        val length = imageData.length

        val data: CPointer<ByteVar> = bytes.reinterpret()
        return ByteArray(length.toInt()) { index -> data[index] }
    }

     fun toBase64(): String {
        val imageData = UIImageJPEGRepresentation(image, 0.99)
            ?: throw IllegalArgumentException("image data is null")

        return imageData.base64EncodedStringWithOptions(0)
    }

     fun toBase64WithCompress(maxSize: Int): String {
        val imageSize = image.size.useContents { this }
        val scale = minOf(maxSize / imageSize.width, maxSize / imageSize.height)

        if (scale > 1) return toBase64()

        val newWidth = imageSize.width * scale
        val newHeight = imageSize.height * scale

        UIGraphicsBeginImageContextWithOptions(CGSizeMake(newWidth, newHeight), false, 0.0)
        image.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
        val newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext();

        val imageData = UIImageJPEGRepresentation(newImage!!, 0.99)
            ?: throw IllegalArgumentException("image data is null")

        return imageData.base64EncodedStringWithOptions(0)
    }
}