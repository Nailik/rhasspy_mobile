package org.rhasspy.mobile.android.content.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.data.resource.StableStringResource

@Composable
fun InformationListElement(
    modifier: Modifier = Modifier,
    text: StableStringResource,
) {
    ListElement(
        modifier = modifier
            .padding(16.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
        icon = { Icon(Icons.Filled.Info, text) },
        text = { Text(text) },
    )
}

@Composable
fun InformationListElement(
    modifier: Modifier = Modifier,
    text: String,
) {
    ListElement(
        modifier = modifier
            .padding(16.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
        icon = { Icon(Icons.Filled.Info, text) },
        text = { Text(text) },
    )
}