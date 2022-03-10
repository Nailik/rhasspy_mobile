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
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.viewModels.LogScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogScreen(viewModel: LogScreenViewModel = viewModel()) {

    val items = viewModel.logArr.observe()

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items) { item ->
            StyledListItem(
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
                text = { Text(text = item.message) },
                secondaryText = { Text(item.time) }
            )
            Divider()
        }
    }

}