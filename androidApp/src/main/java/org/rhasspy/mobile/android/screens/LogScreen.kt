package org.rhasspy.mobile.android.screens

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.logger.ListLogger

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogScreen() {

    val items = ListLogger.logArr.observe()

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items) { item ->
            StyledListItem(
                overlineText = { Text("severity") },
                secondaryText = { Text("tag") },
                singleLineSecondaryText = false,
                text = { Text(text = item) })
            Divider()
        }
    }

}