package org.rhasspy.mobile.android

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiSelector
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.MR
import java.util.regex.Pattern

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    testTag: TestTag,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasTestTag(testTag.name), useUnmergedTree)

fun UiSelector.textMatches(regex: StringResource): UiSelector {
    return UiSelector().textMatches(StringDesc.Resource(MR.strings.microphonePermissionDenied).toString(InstrumentationRegistry.getInstrumentation()
        .targetContext.applicationContext))
}