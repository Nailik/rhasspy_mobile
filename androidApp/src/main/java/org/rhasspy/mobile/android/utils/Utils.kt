package org.rhasspy.mobile.android.utils

import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState.Off
import androidx.compose.ui.state.ToggleableState.On
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.main.LocalNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.CardPaddingLevel0
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.viewModels.AppViewModel

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
    Text(
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
fun Text(
    resource: StringResource,
    vararg args: Any,
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
    Text(
        translate(resource, *args),
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
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}


@Composable
fun Icon(
    imageResource: ImageResource,
    contentDescription: StringResource,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = painterResource(imageResource.drawableResId),
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
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = painter,
        contentDescription = translate(contentDescription),
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun translate(resource: StringResource): String {
    AppViewModel.languageOption.collectAsState().value
    return StringDesc.Resource(resource).toString(LocalContext.current)
}

@Composable
fun translate(resource: StringResource, vararg args: Any): String {
    AppViewModel.languageOption.collectAsState().value
    return StringDesc.ResourceFormatted(resource, *args).toString(LocalContext.current)
}

//https://stackoverflow.com/questions/68389802/how-to-clear-textfield-focus-when-closing-the-keyboard-and-prevent-two-back-pres
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = WindowInsets.isImeVisible
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
fun <E : DataEnum<*>> DropDownEnumListItem(selected: E, label: StringResource, onSelect: (item: E) -> Unit, values: () -> Array<E>) {
    var isExpanded by remember { mutableStateOf(false) }

    ListElement(modifier = Modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = true),
            onClick = { isExpanded = true }
        ),
        text = {
            OutlinedTextField(
                value = translate(selected.text),
                onValueChange = { },
                label = { Text(label) },
                enabled = false,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

            )
        })

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            values().forEach {
                DropdownMenuItem(
                    text = { Text(it.text) },
                    onClick = { isExpanded = false; onSelect(it) })
            }
        }
    }
}

@Composable
fun <E : DataEnum<*>> RadioButtonsEnumSelection(
    modifier: Modifier = Modifier,
    selected: E,
    onSelect: (item: E) -> Unit,
    values: () -> Array<E>,
    content: (@Composable (item: E) -> Unit)? = null
) {
    Card(
        modifier = modifier.padding(CardPaddingLevel0),
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

@Composable
fun <E : DataEnum<*>> RadioButtonsEnumSelectionList(
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
fun SecondaryContent(
    visible: Boolean,
    content: (@Composable () -> Unit)
) {
    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = visible
    ) {
        CompositionLocalProvider(
            LocalAbsoluteTonalElevation provides 0.dp
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                content()
            }
        }
    }
}


@Composable
fun SwitchListItem(
    modifier: Modifier = Modifier,
    text: StringResource,
    secondaryText: StringResource? = null,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)
) {
    ListElement(
        modifier = modifier.clickable { onCheckedChange(!isChecked) },
        text = { Text(text) },
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        trailing = {
            Switch(
                checked = isChecked,
                onCheckedChange = null
            )
        })
}

@Composable
fun ListElement(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    ListItem(
        headlineText = text,
        modifier = modifier,
        overlineText = overlineText,
        supportingText = secondaryText,
        leadingContent = icon,
        trailingContent = trailing
    )
}


@Composable
fun TextFieldListItemVisibility(
    modifier: Modifier = Modifier,
    label: StringResource,
    value: String,
    readOnly: Boolean = false,
    autoCorrect: Boolean = false,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var isShowPassword by rememberSaveable { mutableStateOf(false) }

    TextFieldListItem(
        modifier = modifier,
        label = label,
        value = value,
        readOnly = readOnly,
        autoCorrect = autoCorrect,
        enabled = enabled,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            IconButton(onClick = { isShowPassword = !isShowPassword }) {
                Icon(
                    if (isShowPassword) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    },
                    contentDescription = MR.strings.visibility,
                )
            }
        },
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun TextFieldListItem(
    modifier: Modifier = Modifier,
    label: StringResource,
    value: String,
    readOnly: Boolean = false,
    autoCorrect: Boolean = false,
    enabled: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    ListElement(
        modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
    ) {
        val coroutineScope = rememberCoroutineScope()

        OutlinedTextField(
            singleLine = true,
            value = value,
            readOnly = readOnly,
            enabled = enabled,
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
            keyboardOptions = keyboardOptions.copy(autoCorrect = autoCorrect),
            onValueChange = { onValueChange?.invoke(it) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            modifier = modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss()
                .onFocusEvent {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            label = { Text(label) }
        )
    }
}

@Composable
fun FilledTonalButtonListItem(modifier: Modifier = Modifier, text: StringResource, enabled: Boolean = true, onClick: () -> Unit) {
    ListElement(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = enabled,
                onClick = onClick,
                content = { Text(text) })
        }
    }
}

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
                .padding(PaddingValues(16.dp - 8.dp, 8.dp)), //ListItemHorizontalPadding, ListItemVerticalPadding
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

@Composable
private fun ProvideTextStyleFromToken(
    color: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides color) {
        ProvideTextStyle(textStyle, content)
    }
}

@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: StringResource,
    isChecked: Boolean,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListElement(
        modifier = modifier.clickable(onClick = onClick),
        icon = { RadioButton(selected = isChecked, onClick = onClick) },
        text = { Text(text) },
        trailing = trailing
    )
}

@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListElement(
        modifier = modifier.clickable(onClick = onClick),
        icon = { RadioButton(selected = isChecked, onClick = onClick) },
        text = { Text(text) },
        trailing = trailing
    )
}

fun Boolean.toText(): StringResource {
    return if (this) MR.strings.enabled else MR.strings.disabled
}

@Composable
fun TextWithAction(
    modifier: Modifier = Modifier,
    text: String,
    label: StringResource,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit, icon:
    @Composable () -> Unit
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            singleLine = true,
            value = text,
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
            onValueChange = onValueChange,
            modifier = Modifier
                .clearFocusOnKeyboardDismiss()
                .weight(1f)
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            label = { Text(resource = label) }
        )
        ElevatedButton(
            modifier = Modifier.padding(horizontal = 24.dp),
            onClick = onClick
        ) {
            icon()
        }
    }
}

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = modifier)
}

@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * HTML text to correctly display html
 */
@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier, color: Color) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context).apply { setTextColor(color.toArgb()) } },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}


@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Switch(
        modifier = Modifier
            .clearAndSetSemantics {
                testTag = "SWITCH_TAG"
                role = Role.Switch
                toggleableState = if (checked) {
                    On
                } else {
                    Off
                }
            },
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}


@Composable
fun RowScope.NavigationItem(screen: Enum<*>, icon: @Composable () -> Unit, label: @Composable () -> Unit) {

    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBarItem(
        modifier = Modifier.testTag(screen),
        selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
        onClick = {
            navController.navigate(screen.name) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = icon,
        label = label
    )

}

@Composable
fun OnPauseEffect(onPause: () -> Unit) {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(onPause) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onPause.invoke()
            }
        }
        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}