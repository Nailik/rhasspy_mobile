package org.rhasspy.mobile.android.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.Text


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenItemContent(
    title: StringResource,
    expandedContent: @Composable () -> Unit
) {
    val navigation = LocalMainNavController.current

    Surface(tonalElevation = 1.dp) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                AppBar(
                    title = title,
                    onBackClick = navigation::popBackStack
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                expandedContent()
            }
        }
    }
}

/**
 * top app bar with title and back navigation button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: StringResource, onBackClick: () -> Unit) {

    TopAppBar(
        title = {
            Text(
                resource = title,
                modifier = Modifier.testTag(TestTag.AppBarTitle)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(TestTag.AppBarBackButton)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = MR.strings.back,
                )
            }
        }
    )

}