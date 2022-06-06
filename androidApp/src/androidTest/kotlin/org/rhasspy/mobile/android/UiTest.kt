package org.rhasspy.mobile.android

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import org.junit.Rule
import org.rhasspy.mobile.MR
import kotlin.test.Test


class UiTest {


    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun testExample() {
        val text = StringDesc.Resource(MR.strings.appName).toString(composeTestRule.activity)
        composeTestRule.onNodeWithTag("appName").assert(hasText(text))
    }

}