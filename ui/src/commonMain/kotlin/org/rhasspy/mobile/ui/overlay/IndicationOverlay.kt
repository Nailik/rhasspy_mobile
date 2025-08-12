package org.rhasspy.mobile.ui.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.indication.IndicationState
import org.rhasspy.mobile.data.indication.IndicationState.Idle
import org.rhasspy.mobile.data.indication.IndicationState.Recording
import org.rhasspy.mobile.data.indication.IndicationState.Speaking
import org.rhasspy.mobile.data.indication.IndicationState.Thinking
import org.rhasspy.mobile.data.indication.IndicationState.WakeUp
import org.rhasspy.mobile.resources.assistant_color_four
import org.rhasspy.mobile.resources.assistant_color_one
import org.rhasspy.mobile.resources.assistant_color_three
import org.rhasspy.mobile.resources.assistant_color_two
import org.rhasspy.mobile.ui.LocalViewModelFactory
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.combinedTestTag
import org.rhasspy.mobile.ui.testTag
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel
import kotlin.math.abs

@Composable
fun IndicationOverlayContent() {
    Box(
        modifier = Modifier
            .combinedTestTag(TestTag.Indication, TestTag.Overlay)
            .height(96.dp),
        contentAlignment = Alignment.Center
    ) {
        IndicationContent()
    }
}

@Composable
fun IndicationContent() {

    val viewModel: IndicationOverlayViewModel = LocalViewModelFactory.current.getViewModel()

    val viewState by viewModel.viewState.collectAsState()
    Indication(
        indicationState = viewState.indicationState
    )

}

/**
 * indication animations for overlay
 */
@Composable
private fun Indication(
    indicationState: IndicationState,
) {
    Box(
        modifier = Modifier
            .testTag(TestTag.Indication)
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {

        when (indicationState) {
            Idle -> Unit
            WakeUp -> WakeupIndication()
            Recording -> RecordingIndication()
            Thinking -> ThinkingIndication()
            Speaking -> SpeakingIndication()
        }
    }
}

/**
 * indication when
 */
@Composable
private fun WakeupIndication() {
    Row(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
    ) {
        IndicationBar(1f, MaterialTheme.colorScheme.assistant_color_one)
        IndicationBar(1f, MaterialTheme.colorScheme.assistant_color_two)
        IndicationBar(1f, MaterialTheme.colorScheme.assistant_color_three)
        IndicationBar(1f, MaterialTheme.colorScheme.assistant_color_four)
    }
}

@Composable
private fun RecordingIndication() {
    val infiniteTransition = rememberInfiniteTransition()

    val time = 750
    // Creates a Color animation as a part of the [InfiniteTransition].
    val size by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            // Linearly interpolate between initialValue and targetValue every 1000ms.
            animation = tween(time, easing = LinearEasing),
            // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
            // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
            // [RepeatMode] ensures that the animation value is *always continuous*.
            repeatMode = RepeatMode.Reverse
        )
    )

    val item by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f, // Dark Red
        animationSpec = infiniteRepeatable(
            // Linearly interpolate between initialValue and targetValue every 1000ms.
            animation = tween(time * 8, easing = LinearEasing),
            // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
            // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
            // [RepeatMode] ensures that the animation value is *always continuous*.
            repeatMode = RepeatMode.Restart
        )
    )

    Row(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
    ) {
        IndicationBar(
            if ((0f..1f).contains(item)) size else 1f,
            MaterialTheme.colorScheme.assistant_color_one
        )
        IndicationBar(
            if ((1f..2f).contains(item)) size else 1f,
            MaterialTheme.colorScheme.assistant_color_two
        )
        IndicationBar(
            if ((2f..3f).contains(item)) size else 1f,
            MaterialTheme.colorScheme.assistant_color_three
        )
        IndicationBar(
            if ((3f..4f).contains(item)) size else 1f,
            MaterialTheme.colorScheme.assistant_color_four
        )
    }
}

@Composable
private fun ThinkingIndication() {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            // Linearly interpolate between initialValue and targetValue every 1000ms.
            animation = tween(1500, easing = LinearEasing),
            // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
            // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
            // [RepeatMode] ensures that the animation value is *always continuous*.
            repeatMode = RepeatMode.Restart
        )
    )
    Box(
        modifier = Modifier
            .rotate(rotation)
            .size(48.dp)
    ) {
        IndicationCircle(Alignment.CenterStart, MaterialTheme.colorScheme.assistant_color_one)
        IndicationCircle(Alignment.TopCenter, MaterialTheme.colorScheme.assistant_color_two)
        IndicationCircle(Alignment.CenterEnd, MaterialTheme.colorScheme.assistant_color_three)
        IndicationCircle(Alignment.BottomCenter, MaterialTheme.colorScheme.assistant_color_four)
    }
}

@Composable
private fun SpeakingIndication() {
    val infiniteTransition = rememberInfiniteTransition()

    val item by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 3.5f, // Dark Red
        animationSpec = infiniteRepeatable(
            // Linearly interpolate between initialValue and targetValue every 1000ms.
            animation = tween(1500, easing = LinearEasing),
            // Once [TargetValue] is reached, starts the next iteration in reverse (i.e. from
            // TargetValue to InitialValue). Then again from InitialValue to TargetValue. This
            // [RepeatMode] ensures that the animation value is *always continuous*.
            repeatMode = RepeatMode.Restart
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicationCircle(MaterialTheme.colorScheme.assistant_color_one, 0, item)
        IndicationCircle(MaterialTheme.colorScheme.assistant_color_two, 1, item)
        IndicationCircle(MaterialTheme.colorScheme.assistant_color_three, 2, item)
        IndicationCircle(MaterialTheme.colorScheme.assistant_color_four, 3, item)
    }
}

@Composable
private fun BoxScope.IndicationCircle(alignment: Alignment, color: Color) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .align(alignment)
            .clip(CircleShape)
            .fillMaxHeight()
            .background(color = color)
    )
}

@Composable
private fun RowScope.IndicationBar(weight: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(weight)
            .background(color = color)
    )
}

@Composable
private fun IndicationCircle(color: Color, item: Int, current: Float) {

    fun getHeight(): Float {
        val height = abs(item - current)
        return when {
            height < 1.5f -> height
            else -> 1f
        }
    }

    var height = 60.dp * (1f - getHeight())
    if (height < 12.dp) {
        height = 12.dp
    }

    Box(
        modifier = Modifier
            .padding(6.dp)
            .width(12.dp)
            .height(height)
            .clip(CircleShape)
            .background(color = color)
    )
}