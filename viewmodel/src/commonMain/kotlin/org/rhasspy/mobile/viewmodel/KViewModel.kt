package org.rhasspy.mobile.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action.*
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Consumed
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Consumed.ConsumedMicrophonePermissionSnackBar
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Consumed.ConsumedOverlayPermissionSnackBar
import org.rhasspy.mobile.viewmodel.navigation.Navigator

abstract class KViewModel : ViewModel(), KoinComponent {

    protected val navigator by inject<Navigator>()
    protected val microphonePermission = get<MicrophonePermission>()
    protected val externalResultRequest = get<ExternalResultRequest>()
    private val overlayPermission = get<OverlayPermission>()

    private val _kViewState = MutableStateFlow(
        KViewState(
            isShowMicrophonePermissionInformationDialog = false,
            microphonePermissionSnackBarText = null,
            microphonePermissionSnackBarLabel = null,
            isShowOverlayPermissionInformationDialog = false,
            overlayPermissionSnackBarText = null
        )
    )
    val kViewState = _kViewState.readOnly

    fun composed() {
        navigator.onComposed(this)
    }

    fun disposed() {
        navigator.onDisposed(this)
    }

    fun requireMicrophonePermission(function: () -> Unit) {
        if (microphonePermission.granted.value) {
            function()
        } else {
            onEvent(RequestMicrophonePermission)
        }
    }

    inline fun <reified T> requireOverlayPermission(value: T, function: () -> T): T {
        return if (get<OverlayPermission>().granted.value) {
            function()
        } else {
            onEvent(RequestOverlayPermission)
            value
        }
    }

    fun onEvent(event: KViewModelEvent) {
        when (event) {
            is Action -> onAction(event)
            is Consumed -> onConsumed(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> onRequestMicrophonePermission(false)
            is MicrophonePermissionDialogResult -> {
                _kViewState.update {
                    it.copy(isShowMicrophonePermissionInformationDialog = false)
                }
                if (action.confirm) {
                    onRequestMicrophonePermission(true)
                }
            }


            RequestMicrophonePermissionRedirect -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (externalResultRequest.launch(ExternalResultRequestIntention.RequestMicrophonePermissionExternally) !is ExternalRedirectResult.Success) {
                        _kViewState.update {
                            it.copy(
                                microphonePermissionSnackBarText = MR.strings.microphonePermissionRequestFailed.stable,
                                microphonePermissionSnackBarLabel = null
                            )
                        }
                    }
                }
            }

            RequestOverlayPermission -> onRequestOverlayPermission(false)
            is OverlayPermissionDialogResult -> {
                _kViewState.update {
                    it.copy(isShowOverlayPermissionInformationDialog = false)
                }
                if (action.confirm) {
                    onRequestOverlayPermission(true)
                }
            }
        }
    }

    private fun onConsumed(consumed: Consumed) {
        when (consumed) {
            ConsumedMicrophonePermissionSnackBar -> _kViewState.update {
                it.copy(
                    microphonePermissionSnackBarText = null,
                    microphonePermissionSnackBarLabel = null
                )
            }

            ConsumedOverlayPermissionSnackBar -> _kViewState.update {
                it.copy(overlayPermissionSnackBarText = null)
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
        return if (_kViewState.value.isShowMicrophonePermissionInformationDialog) {
            _kViewState.update { it.copy(isShowMicrophonePermissionInformationDialog = false) }
            true
        } else if (_kViewState.value.isShowOverlayPermissionInformationDialog) {
            _kViewState.update { it.copy(isShowOverlayPermissionInformationDialog = false) }
            true
        } else onBackPressed()
    }

    private fun onRequestMicrophonePermission(hasShownInformation: Boolean) {
        if (!microphonePermission.granted.value) {
            if (!hasShownInformation && microphonePermission.shouldShowInformationDialog()) {
                _kViewState.update { it.copy(isShowMicrophonePermissionInformationDialog = true) }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    microphonePermission.request()

                    if (!microphonePermission.granted.value) {
                        //show snack bar
                        _kViewState.update {
                            it.copy(
                                microphonePermissionSnackBarText = MR.strings.microphonePermissionDenied.stable,
                                microphonePermissionSnackBarLabel = MR.strings.settings.stable
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onRequestOverlayPermission(hasShownInformation: Boolean) {
        if (!overlayPermission.granted.value) {
            if (!hasShownInformation) {
                _kViewState.update { it.copy(isShowOverlayPermissionInformationDialog = true) }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    if (!overlayPermission.request()) {
                        //show snack bar
                        _kViewState.update {
                            it.copy(overlayPermissionSnackBarText = MR.strings.overlayPermissionRequestFailed.stable)
                        }
                    }
                }
            }
        }
    }

}