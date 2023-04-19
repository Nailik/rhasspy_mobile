package org.rhasspy.mobile.android.content.elements

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.rhasspy.mobile.data.resource.StableStringResource

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: StableStringResource,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}


