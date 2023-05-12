package org.rhasspy.mobile.ui.content.elements

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.ImageResource
import org.rhasspy.mobile.ui.utils.toPainter

@Composable
fun LocalImage(
    imageResource: ImageResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    isGreyscale: Boolean = false,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    val painter = remember { imageResource.toPainter(isGreyscale) }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha
    )
}