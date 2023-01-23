package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.android.content.elements.ProvideTextStyleFromToken
import org.rhasspy.mobile.android.content.elements.Text


@Composable
fun SliderListItem(
    modifier: Modifier = Modifier,
    text: StringResource,
    value: Float,
    valueText: String? = null,
    onValueChange: (Float) -> Unit
) {
    //uses custom list item to fix padding for slider
    Surface(
        modifier = modifier,
        shape = RectangleShape, //ListItemDefaults.shape,
        color = MaterialTheme.colorScheme.surface, //ListItemDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface, //ListItemDefaults.contentColor,
        tonalElevation = 0.0.dp, //ListItemDefaults.Elevation,
        shadowElevation = 0.0.dp, //ListItemDefaults.Elevation,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 56.0.dp) //ListTokens.ListItemContainerHeight
                .padding(
                    PaddingValues(
                        16.dp - 8.dp,
                        8.dp
                    )
                ), //ListItemHorizontalPadding, ListItemVerticalPadding
            content = {
                Box(
                    Modifier
                        .weight(1f)
                        .padding(top = 8.dp) //custom
                        .align(Alignment.CenterVertically)
                ) {
                    Column {

                        ProvideTextStyleFromToken(
                            MaterialTheme.colorScheme.onSurface, //colors.headlineColor(enabled = true).value
                            MaterialTheme.typography.bodyLarge
                        ) //ListTokens.ListItemLabelTextFont
                        {
                            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(text)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(valueText ?: "%.2f".format(null, value))
                            }
                        }

                        ProvideTextStyleFromToken(
                            MaterialTheme.colorScheme.onSurfaceVariant, //colors.supportingColor().value
                            MaterialTheme.typography.bodyMedium
                        ) //ListTokens.ListItemSupportingTextFont
                        {
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
