package org.rhasspy.mobile.android.utils

import com.adevinta.android.barista.rule.flaky.FlakyTestRule
import org.junit.Rule
import org.koin.core.component.KoinComponent

abstract class FlakyTest : KoinComponent {

    @get:Rule(order = 1)
    val flakyRule = FlakyTestRule()

}