package org.rhasspy.mobile.android.content.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.android.content.SecondaryContent
import org.rhasspy.mobile.android.content.list.RadioButtonListItem
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.settings.option.IOption

@Composable
fun <E : IOption<*>> RadioButtonsEnumSelectionList(
    modifier: Modifier = Modifier,
    selected: E,
    onSelect: (item: E) -> Unit,
    values: () -> Array<E>
) {
    Column(modifier = modifier) {
        values().forEach {
            RadioButtonListItem(
                modifier = Modifier.testTag(it),
                text = it.text,
                isChecked = selected == it,
            ) {
                onSelect(it)
            }
        }
    }
}

@Composable
fun <E : IOption<*>> RadioButtonsEnumSelection(
    modifier: Modifier = Modifier,
    selected: E,
    onSelect: (item: E) -> Unit,
    values: () -> Array<E>,
    content: (@Composable (item: E) -> Unit)? = null
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        values().forEach {
            Column {
                RadioButtonListItem(
                    modifier = Modifier.testTag(it),
                    text = it.text,
                    isChecked = selected == it,
                ) {
                    onSelect(it)
                }
            }

            content?.also { nullSafeContent ->
                SecondaryContent(visible = selected == it) {
                    nullSafeContent(it)
                }
            }
        }

    }
}

