package org.rhasspy.mobile.ui.utils

import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.ImageResource

expect fun ImageResource.toPainter(isGreyscale: Boolean = false) : Painter