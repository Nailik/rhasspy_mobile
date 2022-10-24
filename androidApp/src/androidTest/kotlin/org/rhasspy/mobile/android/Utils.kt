package org.rhasspy.mobile.android

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.android.configuration.ConfigurationScreens

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    testTag: Enum<*>,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag(testTag.name), useUnmergedTree)
fun UiSelector.text(text: StringResource): UiSelector {
    return this.textMatches(StringDesc.Resource(text).toString(InstrumentationRegistry.getInstrumentation()
        .targetContext.applicationContext))
}

fun SemanticsNodeInteractionsProvider.onNodeWithText(
    text: StringResource,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasText(StringDesc.Resource(text).toString(InstrumentationRegistry.getInstrumentation()
    .targetContext.applicationContext), substring, ignoreCase), useUnmergedTree)