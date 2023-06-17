package org.rhasspy.mobile.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.*
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.Action.RequestMicrophonePermission
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent.Action.RequestOverlayPermission
import org.rhasspy.mobile.viewmodel.navigation.Navigator

abstract class KViewModel : IKViewModel, ViewModel(), KoinComponent {

    protected val navigator by inject<Navigator>()
    protected val microphonePermission = get<MicrophonePermission>()
    protected val externalResultRequest = get<ExternalResultRequest>()
    private val overlayPermission = get<OverlayPermission>()

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

    override fun onEvent(event: KViewModelUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Dialog -> onDialog(event)
            is SnackBar -> onSnackBar(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is Permission -> onPermission(action)
            is Dialog -> onDialog(action)
            is SnackBar -> onSnackBar(action)
        }
    }

    private fun onDialog(dialog: Dialog) {
        _screenViewState.update {
            it.copy(dialogViewState = null)
        }
        when (dialog) {
            Dialog.Cancel -> Unit
            Dialog.Dismiss -> Unit
            Dialog.Submit -> {
                when (_screenViewState.value.dialogViewState) {
                    microphonePermissionInfoDialog -> onRequestMicrophonePermission(true)
                    overlayPermissionInfoDialog -> onRequestOverlayPermission(true)
                }
            }
        }
    }

    private fun onPermission(permission: Permission) {
        when (permission) {
            RequestMicrophonePermission -> onRequestMicrophonePermission(false)
            RequestOverlayPermission -> onRequestOverlayPermission(false)
        }
    }

    private fun onSnackBar(snackBar: SnackBar) {
        _screenViewState.update { it.copy(snackBarViewState = null) }
        when (snackBar) {
            SnackBar.Action -> {
                when (_screenViewState.value.snackBarViewState) {
                    microphonePermissionRequestDenied -> {
                        if (externalResultRequest.launch(ExternalResultRequestIntention.RequestMicrophonePermissionExternally) !is ExternalRedirectResult.Success) {
                            _screenViewState.update {
                                it.copy(snackBarViewState = microphonePermissionRequestFailedSnackBar)
                            }
                        }
                    }
                }
            }

            SnackBar.Consumed -> Unit
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
        return if (_screenViewState.value.dialogViewState != null) {
            _screenViewState.update {
                it.copy(dialogViewState = null)
            }
            true
        } else onBackPressed()
    }

    private fun onRequestMicrophonePermission(hasShownInformation: Boolean) {
        if (!microphonePermission.granted.value) {
            if (!hasShownInformation && microphonePermission.shouldShowInformationDialog()) {
                _screenViewState.update {
                    it.copy(dialogViewState = microphonePermissionInfoDialog)
                }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    microphonePermission.request()

                    if (!microphonePermission.granted.value) {
                        //show snack bar
                        _screenViewState.update {
                            it.copy(snackBarViewState = microphonePermissionRequestDenied)
                        }
                    }
                }
            }
        }
    }

    private fun onRequestOverlayPermission(hasShownInformation: Boolean) {
        if (!overlayPermission.granted.value) {
            if (!hasShownInformation) {
                _screenViewState.update {
                    it.copy(dialogViewState = overlayPermissionInfoDialog)
                }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    if (!overlayPermission.request()) {
                        //show snack bar
                        _screenViewState.update {
                            it.copy(snackBarViewState = overlayPermissionRequestFailedSnackBar)
                        }
                    }
                }
            }
        }
    }

}