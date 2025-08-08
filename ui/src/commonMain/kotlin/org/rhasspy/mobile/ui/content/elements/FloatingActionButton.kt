package org.rhasspy.mobile.ui.content.elements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButton(
    modifier: Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    icon: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(16.0.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = containerColor,
            disabledContainerColor = containerColor.copy(alpha = 0.12f)
        ),
        onClick = onClick,
        enabled = isEnabled,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        content = icon
    )
}