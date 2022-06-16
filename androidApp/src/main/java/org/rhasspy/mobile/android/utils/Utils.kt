package org.rhasspy.mobile.android.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.DataEnum
import org.rhasspy.mobile.observer.Observable
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSetting

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
    AppSettings.languageOption.observe()
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
fun <E : DataEnum<*>> DropDownEnumListItem(selected: E, enabled: Boolean = true, onSelect: (item: E) -> Unit, values: () -> Array<E>) {
    var isExpanded by remember { mutableStateOf(false) }

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
                    enabled = enabled,
                    onClick = { isExpanded = false; onSelect(it) })
            }
        }
    }
}

@Composable
fun DropDownListRemovableWithFileOpen(
    overlineText: @Composable () -> Unit,
    selected: Int? = null,
    enabled: Boolean = true,
    values: Array<Pair<String, Boolean>>,
    onAdd: () -> Unit,
    onRemove: ((index: Int) -> Unit)? = null,
    title: @Composable () -> Unit = { selected?.let { Text(values[it].first) } },
    onSelect: ((index: Int) -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    ListElement(modifier = Modifier
        .clickable { isExpanded = true },
        overlineText = overlineText,
        text = title,
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
            values.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(item.first) },
                    enabled = enabled,
                    trailingIcon = {
                        onRemove?.let { callback ->
                            IconButton(
                                onClick = { callback.invoke(index) },
                                enabled = enabled && !item.second
                            ) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = MR.strings.remove)
                            }
                        }
                    }, onClick = { onSelect?.invoke(index) })
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(alignment = Alignment.Center),
                    border = BorderStroke(ButtonDefaults.outlinedButtonBorder.width, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    onClick = {
                        isExpanded = false
                        onAdd.invoke()
                    })
                {
                    Icon(imageVector = Icons.Filled.FileOpen, contentDescription = MR.strings.expandDropDown)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(MR.strings.selectFile)
                }

            }
        }
    }
}

@Composable
fun DropDownStringList(
    overlineText: @Composable () -> Unit,
    selected: String,
    enabled: Boolean = true,
    values: Array<String>,
    onSelect: (item: String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ListElement(modifier = Modifier
        .clickable { isExpanded = true },
        overlineText = overlineText,
        text = { Text(selected) },
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
            values.forEach { text ->

                DropdownMenuItem(
                    modifier = if (text == selected) Modifier.background(MaterialTheme.colorScheme.surfaceVariant) else Modifier,
                    text = { Text(text) },
                    enabled = enabled,
                    onClick = {
                        isExpanded = false
                        onSelect.invoke(text)
                    })
            }

        }
    }
}

@Composable
fun ExpandableListItem(
    text: StringResource,
    secondaryText: StringResource? = null,
    expandedContent: @Composable () -> Unit
) {
    ExpandableListItemInternal(
        text = text,
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        expandedContent = expandedContent
    )
}

@Composable
fun ExpandableListItemString(
    text: StringResource,
    secondaryText: String? = null,
    expandedContent: @Composable () -> Unit
) {
    ExpandableListItemInternal(
        text = text,
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        expandedContent = expandedContent
    )
}


@Composable
private fun ExpandableListItemInternal(
    text: StringResource,
    secondaryText: (@Composable () -> Unit)?,
    expandedContent: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    ListElement(
        modifier = Modifier
            .clickable { isExpanded = !isExpanded },
        text = { Text(text) },
        secondaryText = secondaryText,
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
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            expandedContent()
        }
    }
}

@Composable
fun SwitchListItem(
    text: StringResource,
    secondaryText: StringResource? = null,
    enabled: Boolean = true,
    isChecked: Boolean, onCheckedChange: ((Boolean) -> Unit)
) {
    ListElement(modifier = Modifier
        .clickable { onCheckedChange(!isChecked) },
        text = { Text(text) },
        secondaryText = secondaryText?.let { { Text(secondaryText) } } ?: run { null },
        trailing = {
            Switch(
                enabled = enabled,
                checked = isChecked,
                onCheckedChange = null
            )
        })
}

@Composable
fun ListElement(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(vertical = 8.dp),
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    StyledListItem(
        modifier = modifier.padding(paddingValues),
        icon = icon,
        secondaryText = secondaryText,
        singleLineSecondaryText = singleLineSecondaryText,
        overlineText = overlineText,
        trailing = trailing,
        text = text
    )
}


@OptIn(ExperimentalFoundationApi::class)
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
    paddingValues: PaddingValues = PaddingValues(vertical = 2.dp),
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    ListElement(
        modifier = modifier.bringIntoViewRequester(bringIntoViewRequester),
        paddingValues = paddingValues
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
            modifier = Modifier
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
fun OutlineButtonListItem(text: StringResource, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        OutlinedButton(enabled = enabled, onClick = onClick) {
            Text(text)
        }
    }
}

@Composable
fun SliderListItem(text: StringResource, value: Float, enabled: Boolean = true, onValueChange: (Float) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text("${translate(text)} ($value)")

        Slider(
            modifier = Modifier.padding(top = 12.dp),
            enabled = enabled,
            value = value,
            onValueChange = onValueChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonListItem(text: StringResource, isChecked: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    ListElement(modifier = Modifier.clickable { onClick() }) {
        Row {
            RadioButton(selected = isChecked, enabled = enabled, onClick = onClick)
            Text(text, modifier = Modifier.weight(1f))
        }
    }
}

fun Boolean.toText(): StringResource {
    return if (this) MR.strings.enabled else MR.strings.disabled
}

@Composable
fun <T> LiveData<T>.observe(): T {
    return this.ld().observeAsState(this.value).value
}

@Composable
fun <T> Observable<T>.observe(): T {
    return this.toLiveData().ld().observeAsState(this.value).value
}


@Composable
fun <T> AppSetting<T>.observe(): T {
    return this.data.toLiveData().observe()
}

@Composable
fun <T> ConfigurationSetting<T>.observeCurrent(): T {
    return this.unsaved.toLiveData().observe()
}


@Composable
fun ColorScheme.toColors(isLight: Boolean): Colors {
    return Colors(
        primary = primary,
        primaryVariant = onPrimaryContainer,
        secondary = secondary,
        secondaryVariant = onSecondaryContainer,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onError = onError,
        isLight = isLight
    )
}


@OptIn(ExperimentalFoundationApi::class)
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
