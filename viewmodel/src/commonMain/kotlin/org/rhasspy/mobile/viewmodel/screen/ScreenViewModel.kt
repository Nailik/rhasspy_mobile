package org.rhasspy.mobile.viewmodel.screen

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention.RequestMicrophonePermissionExternally
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.utils.IOpenLinkUtils
import org.rhasspy.mobile.viewmodel.navigation.INavigator
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Action
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Action.RequestMicrophonePermission
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Action.RequestOverlayPermission
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Confirm
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Dialog.Dismiss
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.SnackBar
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.SnackBar.Consumed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.MicrophonePermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenDialogState.OverlayPermissionInfo
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.LinkOpenFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.MicrophonePermissionRequestDenied
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.MicrophonePermissionRequestFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.OverlayPermissionRequestFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.ScanQRCodeFailed
import org.rhasspy.mobile.viewmodel.screen.ScreenViewState.ScreenSnackBarState.SelectFileFailed

abstract class ScreenViewModel : IScreenViewModel, ViewModel(), KoinComponent {

    protected val navigator by inject<INavigator>()
    private val dispatcher by inject<IDispatcherProvider>()

    protected val microphonePermission = get<IMicrophonePermission>()
    protected val externalResultRequest = get<IExternalResultRequest>()
    private val overlayPermission = get<IOverlayPermission>()
    private val openLinkUtils = get<IOpenLinkUtils>()

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
        return if (overlayPermission.granted.value) {
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
        viewModelScope.launch(dispatcher.IO) {
            FileUtils.selectFile(folderType)?.also(action) ?: run {
                _screenViewState.update { it.copy(snackBarState = SelectFileFailed) }
            }
        }
    }

    fun scanQRCode(onResult: (String) -> Unit) {
        viewModelScope.launch(dispatcher.IO) {
            when (val result =
                externalResultRequest.launchForResult(ExternalResultRequestIntention.ScanQRCode)) {
                is ExternalRedirectResult.Result -> onResult(result.data)
                else -> _screenViewState.update { it.copy(snackBarState = ScanQRCodeFailed) }
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
                    MicrophonePermissionRequestDenied,
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
    override fun onBackPressedClick(): Boolean {
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
                viewModelScope.launch(dispatcher.IO) {
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
                if (!overlayPermission.request()) {
                    //show snack bar
                    _screenViewState.update { it.copy(snackBarState = OverlayPermissionRequestFailed) }
                }
            }
        }
    }

}