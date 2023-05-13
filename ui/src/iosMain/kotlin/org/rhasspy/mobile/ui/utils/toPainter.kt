package org.rhasspy.mobile.ui.utils

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.icerock.moko.resources.ImageResource
import androidx.compose.ui.graphics.painter.BitmapPainter
import org.rhasspy.mobile.resources.MR

actual fun ImageResource.toPainter(isGreyscale: Boolean) : Painter {
    return MR.images.ic_launcher.toUIImage()?.toSkiaImage(isGreyscale)?.toComposeImageBitmap()?.let(::BitmapPainter)!!
}