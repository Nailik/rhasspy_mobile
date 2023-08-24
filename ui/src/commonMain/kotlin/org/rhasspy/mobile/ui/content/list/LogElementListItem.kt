package org.rhasspy.mobile.ui.content.list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import database.LogElements
import org.rhasspy.mobile.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogListElement(item: LogElements) {
    ListElement(
        overlineText = {
            Row {
                Text(
                    text = item.tag,
                    modifier = Modifier.weight(1f)
                )

                val severity = Severity.entries[item.severity.toInt()]

                val color = when (severity) {
                    Severity.Verbose -> MaterialTheme.colorScheme.color_verbose
                    Severity.Debug   -> MaterialTheme.colorScheme.color_debug
                    Severity.Info    -> MaterialTheme.colorScheme.color_info
                    Severity.Warn    -> MaterialTheme.colorScheme.color_warn
                    Severity.Error   -> MaterialTheme.colorScheme.color_error
                    Severity.Assert  -> MaterialTheme.colorScheme.color_assert
                }

                Badge(
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    containerColor = color,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = severity.name,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(4.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        },
        text = {
            Text(text = "${item.message}${item.throwable?.let { "\n$it" } ?: ""}")
        },
        secondaryText = {
            Text(item.time)
        }
    )
}