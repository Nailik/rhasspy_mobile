package androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

private const val CONTENT_ANIMATION_DURATION = 100

@OptIn(ExperimentalAnimationApi::class)
fun horizontalAnimationSpec(targetOrdinal: Int, initialOrdinal: Int): ContentTransform {
    return if (targetOrdinal > initialOrdinal) {
        slideInHorizontally(
            animationSpec = tween(CONTENT_ANIMATION_DURATION),
            initialOffsetX = { fullWidth -> fullWidth }
        ) togetherWith slideOutHorizontally(
            animationSpec = tween(CONTENT_ANIMATION_DURATION),
            targetOffsetX = { fullWidth -> -fullWidth })
    } else {
        slideInHorizontally(
            animationSpec = tween(CONTENT_ANIMATION_DURATION),
            initialOffsetX = { fullWidth -> -fullWidth }
        ) togetherWith slideOutHorizontally(
            animationSpec = tween(CONTENT_ANIMATION_DURATION),
            targetOffsetX = { fullWidth -> fullWidth })
    }
}