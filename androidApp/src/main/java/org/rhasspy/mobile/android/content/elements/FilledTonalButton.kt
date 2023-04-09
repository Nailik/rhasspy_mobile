package org.rhasspy.mobile.android.content.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.resource.StableStringResource

@Composable
fun FilledTonalButton(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: StableStringResource,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    androidx.compose.material3.FilledTonalButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        content = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                icon?.also {
                    Icon(
                        imageVector = icon,
                        contentDescription = text
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(resource = text)
            }
        })
}
