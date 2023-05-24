package org.rhasspy.mobile.viewmodel.screens.about

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Action.OpenSourceCode
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screens.about.AboutScreenUiEvent.Consumed.ShowSnackBar

/**
 * For About screen that displays app information
 * Holds changelog text and action to open source code link
 */
@Stable
class AboutScreenViewModel(
    viewStateCreator: AboutScreenViewStateCreator
) : KViewModel() {

    private val _viewState: MutableStateFlow<AboutScreenViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: AboutScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Change -> onChange(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenSourceCode -> openSourceCode()
            BackClick -> navigator.onBackPressed()
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                CloseChangelog -> it.copy(isChangelogDialogVisible = false)
                CloseDataPrivacy -> it.copy(isPrivacyDialogVisible = false)
                CloseLibrary -> it.copy(isLibraryDialogVisible = false)
                OpenChangelog -> it.copy(isChangelogDialogVisible = true)
                OpenDataPrivacy -> it.copy(isPrivacyDialogVisible = true)
                is OpenLibrary -> it.copy(
                    isLibraryDialogVisible = true,
                    libraryDialogContent = change.library
                )
            }
        }
    }

    private fun onConsumed(consumed: Consumed) {
        _viewState.update {
            when (consumed) {
                ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun openSourceCode() {
        if (!get<OpenLinkUtils>().openLink(LinkType.SourceCode)) {
            _viewState.update {
                it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return when {
            _viewState.value.isChangelogDialogVisible -> {
                _viewState.update { it.copy(isChangelogDialogVisible = false) }
                true
            }

            _viewState.value.isPrivacyDialogVisible -> {
                _viewState.update { it.copy(isPrivacyDialogVisible = false) }
                true
            }

            _viewState.value.isLibraryDialogVisible -> {
                _viewState.update { it.copy(isLibraryDialogVisible = false) }
                true
            }

            else -> false
        }
    }

}