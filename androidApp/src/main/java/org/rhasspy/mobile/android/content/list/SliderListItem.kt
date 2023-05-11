package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.ui.content.elements.ProvideTextStyleFromToken
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.data.resource.StableStringResource


@Composable
fun SliderListItem(
    modifier: Modifier = Modifier,
    text: StableStringResource,
    value: Float,
    valueText: String? = null,
    onValueChange: (Float) -> Unit
) {
    //uses custom list item to fix padding for slider
    Surface(
        modifier = modifier,
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.0.dp,
        shadowElevation = 0.0.dp,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 56.0.dp)
                .padding(
                    PaddingValues(
                        16.dp - 8.dp,
                        8.dp
                    )
                ),
            content = {
                Box(
                    Modifier
                        .weight(1f)
                        .padding(top = 8.dp) //custom
                        .align(Alignment.CenterVertically)
                ) {
                    Column {

                        ProvideTextStyleFromToken(
                            MaterialTheme.colorScheme.onSurface,
                            MaterialTheme.typography.bodyLarge
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(text)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(valueText ?: "%.2f".format(null, value))
                            }
                        }

                        ProvideTextStyleFromToken(
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            MaterialTheme.typography.bodyMedium
                        ) {
                            Slider(
                                value = value,
                                onValueChange = onValueChange
                            )
                        }

                    }
                }
            }
        )
    }
}
