package org.rhasspy.mobile.android.content.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import org.rhasspy.mobile.android.content.SecondaryContent
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.combinedTestTag
import org.rhasspy.mobile.ui.content.list.RadioButtonListItem
import org.rhasspy.mobile.ui.testTag

@Composable
fun <E : IOption<*>> RadioButtonsEnumSelectionList(
    modifier: Modifier = Modifier,
    selected: E,
    onSelect: (item: E) -> Unit,
    combinedTestTag: TestTag? = null,
    values: ImmutableList<E>
) {
    Column(modifier = modifier) {
        values.forEach { item ->
            RadioButtonListItem(
                modifier = Modifier.let {
                    combinedTestTag?.let { tag ->
                        it.combinedTestTag(item, tag)
                    } ?: it.testTag(item)
                },
                text = item.text,
                isChecked = selected == item,
                onClick = { onSelect(item) }
            )
        }
    }
}

@Composable
fun <E : IOption<*>> RadioButtonsEnumSelection(
    modifier: Modifier = Modifier,
    selected: E,
    onSelect: (item: E) -> Unit,
    values: ImmutableList<E>,
    content: (@Composable (item: E) -> Unit)? = null
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            values.forEach { item ->
                Column {
                    RadioButtonListItem(
                        modifier = Modifier.testTag(item),
                        text = item.text,
                        isChecked = selected == item,
                        onClick = { onSelect(item) }
                    )
                }

                content?.also { nullSafeContent ->
                    SecondaryContent(
                        visible = selected == item,
                        content = { nullSafeContent(item) }
                    )
                }
            }

        }
    }
}


@Composable
fun <E : IOption<*>> RadioButtonsEnumSelection(
    modifier: Modifier = Modifier,
    selected: E,
    onSelect: (item: E) -> Unit,
    values: PersistentList<E>,
    secondaryContentVisible: Boolean,
    content: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            values.forEach { item ->
                RadioButtonListItem(
                    modifier = Modifier.testTag(item),
                    text = item.text,
                    isChecked = selected == item,
                    onClick = { onSelect(item) }
                )
            }

            content?.also { nullSafeContent ->
                SecondaryContent(
                    visible = secondaryContentVisible,
                    content = { nullSafeContent() }
                )
            }

        }
    }
}