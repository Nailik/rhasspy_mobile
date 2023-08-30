package org.rhasspy.mobile.ui.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    screenViewModel: IScreenViewModel,
    title: StableStringResource,
    onBackClick: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = title,
                onBackClick = onBackClick
            )
        },
        content = { paddingValues ->
            ScreenContent(
                modifier = Modifier.padding(paddingValues),
                screenViewModel = screenViewModel,
                content = content
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton
    )

}