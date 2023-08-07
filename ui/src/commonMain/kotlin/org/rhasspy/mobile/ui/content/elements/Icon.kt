package org.rhasspy.mobile.ui.content.elements

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.rhasspy.mobile.data.resource.StableStringResource

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: StableStringResource,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = imageVector,
        contentDescription = Translate.translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}