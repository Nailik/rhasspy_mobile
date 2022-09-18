package org.rhasspy.mobile.android.uiservices

import android.util.Range
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import kotlin.math.abs

@Preview
@Composable
fun WakeupIndication() {
    Row(
        modifier = Modifier
            .height(6.dp)
            .fillMaxWidth()
    ) {
        IndicationBar(1f, Range(0f, 1f), 1f, assistant_color_one)
        IndicationBar(1f, Range(1f, 2f), 1f, assistant_color_two)
        IndicationBar(1f, Range(2f, 3f), 1f, assistant_color_three)
        IndicationBar(1f, Range(3f, 4f), 1f, assistant_color_four)
    }
}

@Preview
@Composable
fun RecordingIndication() {
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
            .height(6.dp)
            .fillMaxWidth()
    ) {
        IndicationBar(item, Range(0f, 1f), size, assistant_color_one)
        IndicationBar(item, Range(1f, 2f), size, assistant_color_two)
        IndicationBar(item, Range(2f, 3f), size, assistant_color_three)
        IndicationBar(item, Range(3f, 4f), size, assistant_color_four)
    }
}

@Preview
@Composable
fun ThinkingIndication() {
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
        IndicationCircle(Alignment.CenterStart, assistant_color_one)
        IndicationCircle(Alignment.TopCenter, assistant_color_two)
        IndicationCircle(Alignment.CenterEnd, assistant_color_three)
        IndicationCircle(Alignment.BottomCenter, assistant_color_four)
    }
}

@Preview
@Composable
fun SpeakingIndication() {
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
            .height(80.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicationCircle(assistant_color_one, 0, item)
        IndicationCircle(assistant_color_two, 1, item)
        IndicationCircle(assistant_color_three, 2, item)
        IndicationCircle(assistant_color_four, 3, item)
    }
}

@Composable
fun BoxScope.IndicationCircle(alignment: Alignment, color: Color) {
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
fun RowScope.IndicationBar(item: Float, range: Range<Float>, size: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(if (range.contains(item)) size else 1f)
            .background(color = color)
    )
}

@Composable
fun IndicationCircle(color: Color, item: Int, current: Float) {

    fun getHeight(): Float {
        val height = abs(item - current)
        return when {
            height < 1.5f -> height
            else -> 1f
        }
    }

    var height = 60.dp * (1f - getHeight())
    if(height < 12.dp){
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