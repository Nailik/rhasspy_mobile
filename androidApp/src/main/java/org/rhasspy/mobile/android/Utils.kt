package org.rhasspy.mobile.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.DataEnum

val lang = mutableStateOf(StringDesc.localeType)

@Composable
fun Text(
    resource: StringResource,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    androidx.compose.material3.Text(
        translate(resource),
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        onTextLayout,
        style
    )
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: StringResource,
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun Icon(
    painter: Painter,
    contentDescription: StringResource,
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        painter = painter,
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun translate(resource: StringResource): String {
    lang.value
    return StringDesc.Resource(resource).toString(LocalContext.current)
}

//https://stackoverflow.com/questions/68389802/how-to-clear-textfield-focus-when-closing-the-keyboard-and-prevent-two-back-pres
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = LocalWindowInsets.current.ime.isVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@Composable
fun IndicatedSmallIcon(isIndicated: Boolean, rotationTarget: Float = 180f, icon: @Composable (modifier: Modifier) -> Unit) {
    val rotation = animateFloatAsState(
        targetValue = if (isIndicated) {
            rotationTarget
        } else 0f,
        animationSpec = tween(300)
    )
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (isIndicated) 1f else 0f,
        animationSpec = tween(100)
    )
    Box(
        Modifier
            .background(
                color = NavigationBarItemDefaults.colors().indicatorColor.copy(alpha = animationProgress),
                shape = RoundedCornerShape(16.0.dp)
            )
            .padding(horizontal = 8.dp)
    ) {
        icon(Modifier.rotate(rotation.value))
    }
}

@Composable
fun <E : DataEnum> DropDownEnumListItem(initial: E, values: () -> Array<E>) {
    var isExpanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(initial) }


    ListElement(modifier = Modifier
        .clickable { isExpanded = true },
        text = { Text(selected.text) },
        trailing = {
            IndicatedSmallIcon(isExpanded) {
                Icon(
                    modifier = it,
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = MR.strings.expandDropDown,
                )
            }
        })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            values().forEach {
                DropdownMenuItem(
                    text = { Text(it.text) },
                    onClick = { selected = it })
            }
        }
    }
}

@Composable
fun ExpandableListItem(text: StringResource, expandedContent: @Composable () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    ListElement(
        modifier = Modifier
            .clickable { isExpanded = !isExpanded },
        text = { Text(text) },
        trailing = {
            IndicatedSmallIcon(isExpanded) {
                Icon(
                    modifier = it,
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = MR.strings.expandListItem
                )
            }
        }
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = isExpanded
    ) {
        expandedContent()
    }
}

@Composable
fun SwitchListItem(text: StringResource) {
    var isChecked by remember { mutableStateOf(false) }

    ListElement(modifier = Modifier
        .clickable { isChecked = !isChecked },
        text = { Text(text) },
        trailing = {
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = !isChecked })
        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListElement(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    ListItem(
        modifier = modifier.padding(vertical = 8.dp),
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = trailing,
        text = text
    )
}