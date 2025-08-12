package org.rhasspy.mobile.android.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.adevinta.android.barista.rule.flaky.FlakyTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.component.KoinComponent

abstract class FlakyTest : KoinComponent {

    @get:Rule
    val flakyRule = FlakyTestRule()

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    lateinit var scenario: ActivityScenario<ComponentActivity>
    lateinit var activity: ComponentActivity

    @Composable
    abstract fun ComposableContent()

    open fun onBefore() {}

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(ComponentActivity::class.java)
    }

    fun setupContent() {
        onBefore()
        scenario.onActivity { activity ->
            this.activity = activity
            activity.setContent {
                TestContentProvider {
                    ComposableContent()
                }
            }
        }
    }

    @After
    fun tearDown() {
        scenario.close()
    }

}