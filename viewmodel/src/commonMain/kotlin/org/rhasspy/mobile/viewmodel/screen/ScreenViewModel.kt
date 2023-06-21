package org.rhasspy.mobile.viewmodel.screen

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention.RequestMicrophonePermissionExternally
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Action.RequestMicrophonePermission
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Action.RequestOverlayPermission
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Dismiss
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.SnackBar.Consumed

abstract class ScreenViewModel : IScreenViewModel, ViewModel(), KoinComponent {

    protected val navigator by inject<Navigator>()
    protected val microphonePermission = get<MicrophonePermission>()
    protected val externalResultRequest = get<ExternalResultRequest>()
    private val overlayPermission = get<OverlayPermission>()
    private val openLinkUtils = get<OpenLinkUtils>()

    private val _screenViewState = MutableStateFlow(ScreenViewState())
    override val screenViewState = _screenViewState.readOnly

    override fun onComposed() = navigator.onComposed(this)
    override fun onDisposed() = navigator.onDisposed(this)

    fun requireMicrophonePermission(function: () -> Unit) {
        if (microphonePermission.granted.value) {
            function()
        } else {
            onEvent(RequestMicrophonePermission)
        }
    }

    fun requireOverlayPermission(function: () -> Unit) {
        return if (get<OverlayPermission>().granted.value) {
            function()
        } else {
            onEvent(RequestOverlayPermission)
        }
    }

    fun openLink(linkType: LinkType) {
        if (!openLinkUtils.openLink(linkType)) {
            _screenViewState.update { it.copy(snackBarState = LinkOpenFailed) }
        }
    }

    fun selectFile(folderType: FolderType, action: (Path) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.selectFile(folderType)?.also(action) ?: run {
                _screenViewState.update { it.copy(snackBarState = SelectFileFailed) }
            }
        }
    }

    override fun onEvent(event: ScreenViewModelUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Dialog -> onDialog(event)
            is SnackBar -> onSnackBar(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> onRequestMicrophonePermission(false)
            RequestOverlayPermission -> onRequestOverlayPermission(false)
        }
    }

    private fun onDialog(dialog: Dialog) {
        _screenViewState.update {
            it.copy(dialogState = null)
        }
        when (dialog) {
            is Dismiss -> Unit
            is Confirm -> {
                when (dialog.dialogState) {
                    MicrophonePermissionInfo -> onRequestMicrophonePermission(true)
                    OverlayPermissionInfo -> onRequestOverlayPermission(true)
                }
            }
        }
    }

    private fun onSnackBar(snackBar: SnackBar) {
        _screenViewState.update { it.copy(snackBarState = null) }
        when (snackBar) {
            Consumed -> Unit
            is SnackBar.Action -> {
                when (snackBar.snackBarState) {
                    MicrophonePermissionRequestFailed ->
                        if (externalResultRequest.launch(RequestMicrophonePermissionExternally) !is ExternalRedirectResult.Success) {
                            _screenViewState.update { it.copy(snackBarState = MicrophonePermissionRequestFailed) }
                        }

                    else -> Unit
                }
            }
        }
    }

    /**
     * returns true if pop back stack was handled internally
     */
    open fun onBackPressed(): Boolean {
        return false
    }


    /**
     * returns true if pop back stack was handled internally
     */
    fun onBackPressedClick(): Boolean {
        return if (_screenViewState.value.dialogState != null) {
            _screenViewState.update { it.copy(dialogState = null) }
            true
        } else onBackPressed()
    }

    private fun onRequestMicrophonePermission(hasShownInformation: Boolean) {
        if (!microphonePermission.granted.value) {
            if (!hasShownInformation && microphonePermission.shouldShowInformationDialog()) {
                _screenViewState.update { it.copy(dialogState = MicrophonePermissionInfo) }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    microphonePermission.request()

                    if (!microphonePermission.granted.value) {
                        //show snack bar
                        _screenViewState.update { it.copy(snackBarState = MicrophonePermissionRequestDenied) }
                    }
                }
            }
        }
    }

    private fun onRequestOverlayPermission(hasShownInformation: Boolean) {
        if (!overlayPermission.granted.value) {
            if (!hasShownInformation) {
                _screenViewState.update { it.copy(dialogState = OverlayPermissionInfo) }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    if (!overlayPermission.request()) {
                        //show snack bar
                        _screenViewState.update { it.copy(snackBarState = OverlayPermissionRequestFailed) }
                    }
                }
            }
        }
    }

}