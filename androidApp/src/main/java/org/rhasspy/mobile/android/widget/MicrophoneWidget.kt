package org.rhasspy.mobile.android.widget

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.fillMaxSize
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.android.theme.DarkThemeColors
import org.rhasspy.mobile.viewModels.HomeScreenViewModel

class MicrophoneWidget : GlanceAppWidget() {

    @Composable
    @GlanceComposable
    override fun Content() {
        MaterialTheme(DarkThemeColors) {
            Button(
                text = "text",
                onClick = actionRunCallback<TestCallback>(),
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadiusCompat(8, MaterialTheme.colorScheme.error.toArgb())
            )
        }
    }

    /**
     * Adds rounded corners for the current view.
     *
     * On S+ it uses [GlanceModifier.cornerRadius]
     * on <S it creates [ShapeDrawable] and sets background
     *
     * @param cornerRadius [Int] radius set to all corners of the view.
     * @param color [Int] value of a color that will be set as background
     * @param backgroundAlpha [Float] value of an alpha that will be set to background color - defaults to 1f
     */
    private fun GlanceModifier.cornerRadiusCompat(
        cornerRadius: Int,
        @ColorInt color: Int,
        @FloatRange(from = 0.0, to = 1.0) backgroundAlpha: Float = 1f,
    ): GlanceModifier {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.background(Color(color).copy(alpha = backgroundAlpha))
                .cornerRadius(cornerRadius.dp)
        } else {
            val radii = FloatArray(8) { cornerRadius.toFloat() }
            val shape = ShapeDrawable(RoundRectShape(radii, null, null))
            shape.paint.color = ColorUtils.setAlphaComponent(color, (255 * backgroundAlpha).toInt())
            val bitmap = shape.toBitmap(width = 150, height = 75)
            this.background(BitmapImageProvider(bitmap))
        }
    }





}


class TestCallback : ActionCallback, KoinComponent {


    private val viewModel = get<HomeScreenViewModel>()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        viewModel.toggleSession()
    }
}
