package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.BuildKonfig
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.platformspecific.resource.readToString

@Stable
data class AboutScreenViewState internal constructor(
    val changelog: ImmutableList<String>,
    val privacy: String,
    val libraries: ImmutableList<Library>
) {

    companion object : KoinComponent{
        fun getInitialViewState(): AboutScreenViewState {
            return AboutScreenViewState(
                changelog = BuildKonfig.changelog.split("\\\\")
                    .map { it.replace("\n", "") }
                    .filter { it.isNotEmpty() }
                    .map { "· $it" }
                    .toImmutableList(),
                privacy = MR.files.dataprivacy.readToString(get()),
                libraries = Libs.Builder().withJson(MR.files.aboutlibraries.readToString(get())).build().libraries.toImmutableList()
            )
        }
    }

}