package org.rhasspy.mobile.ui.content

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.domain.DomainState.*
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.Text
import org.rhasspy.mobile.ui.content.list.ListElement

@Composable
fun DomainStateHeaderItem(
    modifier: Modifier = Modifier,
    domainStateFlow: StateFlow<DomainState>,
) {
    val domainState by domainStateFlow.collectAsState()

    AnimatedVisibility(
        modifier = modifier,
        visible = domainState != NoError,
        enter = fadeIn() + expandIn(
            expandFrom = Alignment.TopCenter,
            initialSize = { fullSize -> IntSize(fullSize.width, 0) },
        ),
        exit = shrinkOut(
            shrinkTowards = Alignment.TopCenter,
            targetSize = { fullSize -> IntSize(fullSize.width, 0) }
        ) + fadeOut(),
    ) {

        when (val state = domainState) {
            is Error   -> DomainStateHeaderItemError(modifier, state)
            is Loading -> DomainStateHeaderItemLoading()
            is NoError -> Unit
        }
    }

}

@Composable
fun DomainStateIcon(
    errorStateFlow: StateFlow<DomainState>,
) {
    val domainState by errorStateFlow.collectAsState()

    when (domainState) {
        is Error   -> Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = MR.strings.error.stable,
            tint = MaterialTheme.colorScheme.errorContainer,
        )

        is Loading -> CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.errorContainer,
        )
        is NoError -> Unit
    }

}

@Composable
private fun DomainStateHeaderItemError(
    modifier: Modifier = Modifier,
    error: Error,
) {
    ListElement(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            headlineColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconColor = MaterialTheme.colorScheme.onErrorContainer,
            supportingColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        icon = {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = MR.strings.error.stable,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
        },
        text = { Text(MR.strings.error.stable) },
        secondaryText = {
            Text(wrapper = error.textWrapper)
        }
    )
}

@Composable
private fun DomainStateHeaderItemLoading() {
    ListElement(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            headlineColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconColor = MaterialTheme.colorScheme.onErrorContainer,
            supportingColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        icon = {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        },
        text = { Text(MR.strings.loading.stable) },
    )
}