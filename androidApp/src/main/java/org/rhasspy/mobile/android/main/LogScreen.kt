package org.rhasspy.mobile.android.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Severity
import org.koin.androidx.compose.getViewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.theme.*
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.viewModels.LogScreenViewModel

/**
 * show log information
 */
@Preview
@Composable
fun LogScreen(viewModel: LogScreenViewModel = getViewModel()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(viewModel) },
    ) { paddingValues ->

        Surface(Modifier.padding(paddingValues)) {
            LogScreenContent(viewModel)
        }

    }
}

@Composable
private fun AppBar(viewModel: LogScreenViewModel) {
    TopAppBar(modifier = Modifier,
        title = { org.rhasspy.mobile.android.utils.Text(MR.strings.appName, modifier = Modifier.testTag("appName")) },
        actions = {
            Row(modifier = Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LogScreenActions(viewModel)
            }
        }
    )
}

@Composable
fun LogScreenContent(viewModel: LogScreenViewModel) {

    val items = viewModel.logArr.collectAsState().value

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(items) { item ->
            ListElement(
                overlineText = {
                    Row {
                        Text(
                            text = item.tag,
                            modifier = Modifier.weight(1f)
                        )

                        val color = when (item.severity) {
                            Severity.Verbose -> MaterialTheme.colorScheme.color_verbose
                            Severity.Debug -> MaterialTheme.colorScheme.color_debug
                            Severity.Info -> MaterialTheme.colorScheme.color_info
                            Severity.Warn -> MaterialTheme.colorScheme.color_warn
                            Severity.Error -> MaterialTheme.colorScheme.color_error
                            Severity.Assert -> MaterialTheme.colorScheme.color_assert
                            else -> MaterialTheme.colorScheme.color_unknown
                        }

                        Badge(
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                            containerColor = color,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = item.severity.name,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(4.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                },
                text = {
                    Text(text = "${item.message}${item.throwable?.let { "\n$it" } ?: run { "" }}")
                },
                secondaryText = {
                    Text(item.time)
                }
            )
            CustomDivider()
        }
    }

}

/**
 * log screen actions to save and share log file
 */
@Composable
fun LogScreenActions(viewModel: LogScreenViewModel) {

    Row(modifier = Modifier.padding(start = 8.dp)) {

        IconButton(onClick = viewModel::shareLogFile) {
            Icon(imageVector = Icons.Filled.Share, contentDescription = MR.strings.share)
        }

        IconButton(onClick = viewModel::saveLogFile) {
            Icon(imageVector = Icons.Filled.Save, contentDescription = MR.strings.save)
        }

    }

}