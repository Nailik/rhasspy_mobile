package org.rhasspy.mobile.android.uiservices

import android.util.Range
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.android.theme.assistant_color_four
import org.rhasspy.mobile.android.theme.assistant_color_one
import org.rhasspy.mobile.android.theme.assistant_color_three
import org.rhasspy.mobile.android.theme.assistant_color_two
import org.rhasspy.mobile.services.indication.IndicationState
import kotlin.math.abs

/**
 * indication animations for overlay
 */
@Composable
fun Indication(indicationState: IndicationState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        contentAlignment = Alignment.Center
    ) {

        when (indicationState) {
            IndicationState.Idle -> {}
            IndicationState.WakeUp -> WakeupIndication()
            IndicationState.Recording -> RecordingIndication()
            IndicationState.Thinking -> ThinkingIndication()
            IndicationState.Speaking -> SpeakingIndication()
        }
    }
}

/**
 * indication when
 */
@Preview
@Composable
private fun WakeupIndication() {
    Row(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
    ) {
        IndicationBar(1f, Range(0f, 1f), 1f, MaterialTheme.colorScheme.assistant_color_one)
        IndicationBar(1f, Range(1f, 2f), 1f, MaterialTheme.colorScheme.assistant_color_two)
        IndicationBar(1f, Range(2f, 3f), 1f, MaterialTheme.colorScheme.assistant_color_three)
        IndicationBar(1f, Range(3f, 4f), 1f, MaterialTheme.colorScheme.assistant_color_four)
    }
}

@Preview
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
        IndicationBar(item, Range(0f, 1f), size, MaterialTheme.colorScheme.assistant_color_one)
        IndicationBar(item, Range(1f, 2f), size, MaterialTheme.colorScheme.assistant_color_two)
        IndicationBar(item, Range(2f, 3f), size, MaterialTheme.colorScheme.assistant_color_three)
        IndicationBar(item, Range(3f, 4f), size, MaterialTheme.colorScheme.assistant_color_four)
    }
}

@Preview
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

@Preview
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
private fun RowScope.IndicationBar(item: Float, range: Range<Float>, size: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(if (range.contains(item)) size else 1f)
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