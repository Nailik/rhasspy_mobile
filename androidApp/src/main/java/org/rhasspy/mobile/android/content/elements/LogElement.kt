package org.rhasspy.mobile.android.content.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import org.rhasspy.mobile.android.content.list.ListElement
import org.rhasspy.mobile.android.theme.*
import org.rhasspy.mobile.logger.LogElement

@Composable
fun LogListElement(item: LogElement) {
    ListElement(
        overlineText = {
            Row {
                androidx.compose.material3.Text(
                    text = item.tag,
                    modifier = Modifier.weight(1f)
                )

                val color = when (item.severity) {
                    Severity.Verbose -> MaterialTheme.colorScheme.color_verbose
                    Severity.Debug -> MaterialTheme.colorScheme.color_debug
                    Severity.Info -> MaterialTheme.colorScheme.color_info
                    Severity.Warn -> MaterialTheme.colorScheme.color_warn
                    Severity.Error -> MaterialTheme.colorScheme.color_error
                    Severity.Assert -> MaterialTheme.colorScheme.color_assert
                    else -> MaterialTheme.colorScheme.color_unknown
                }

                Badge(
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    containerColor = color,
                    modifier = Modifier.wrapContentSize()
                ) {
                    androidx.compose.material3.Text(
                        text = item.severity.name,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(4.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        },
        text = {
            androidx.compose.material3.Text(text = "${item.message}${item.throwable?.let { "\n$it" } ?: run { "" }}")
        },
        secondaryText = {
            androidx.compose.material3.Text(item.time)
        }
    )
}