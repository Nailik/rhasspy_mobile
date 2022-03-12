package org.rhasspy.mobile.android.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Severity
import org.rhasspy.mobile.android.theme.*
import org.rhasspy.mobile.android.utils.StyledListItem
import org.rhasspy.mobile.android.utils.observe
import org.rhasspy.mobile.viewModels.LogScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogScreen(viewModel: LogScreenViewModel = viewModel()) {

    val items = viewModel.logArr.observe()

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items) { item ->
            StyledListItem(
                modifier = Modifier.drawBehind {
                    val canvasWidth = 8.dp
                    val canvasHeight = size.height
                    drawRoundRect(
                        cornerRadius = CornerRadius(2.dp.toPx()),
                        color = when (item.severity) {
                            Severity.Verbose -> color_verbose
                            Severity.Debug -> color_debug
                            Severity.Info -> color_info
                            Severity.Warn -> color_warn
                            Severity.Error -> color_error
                            Severity.Assert -> color_assert
                            else -> color_unknown
                        },
                        topLeft = Offset(x = 0f, y = 0f),
                        size = Size(canvasWidth.toPx(), canvasHeight)
                    )
                },
                overlineText = {
                    Row {
                        Text(
                            text = item.tag,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.severity.name,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                },
                text = {
                    Text(text = "${item.message}${item.throwable?.let { "\n$it" } ?: run { "" }}")
                },
                secondaryText = { Text(item.time) }
            )
            Divider()
        }
    }

}