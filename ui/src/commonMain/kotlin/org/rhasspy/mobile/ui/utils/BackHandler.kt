package org.rhasspy.mobile.ui.utils

import androidx.compose.runtime.Composable

@Composable
expect fun BackPressHandler(onBackClick: () -> Unit)