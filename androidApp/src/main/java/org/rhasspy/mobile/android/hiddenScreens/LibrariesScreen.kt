package org.rhasspy.mobile.android.hiddenScreens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Logger
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import org.rhasspy.mobile.android.utils.CustomDivider
import org.rhasspy.mobile.android.utils.StyledListItem
import org.rhasspy.mobile.android.utils.observe
import org.rhasspy.mobile.viewModels.LibrariesScreenViewModel

private val logger = Logger.withTag("LibrariesScreen")

@Composable
fun LibrariesScreen() {

    LibrariesContainer(
        Modifier.fillMaxSize()
    )

}
