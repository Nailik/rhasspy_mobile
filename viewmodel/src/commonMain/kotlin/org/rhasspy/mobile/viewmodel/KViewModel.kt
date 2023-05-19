package org.rhasspy.mobile.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Action.*
import org.rhasspy.mobile.viewmodel.KViewModelEvent.Consumed.ConsumedMicrophonePermissionSnackBar
import org.rhasspy.mobile.viewmodel.navigation.Navigator

abstract class KViewModel : ViewModel(), KoinComponent {

    protected val navigator by inject<Navigator>()

    private val _kViewState = MutableStateFlow(
        KViewState(
            isShowMicrophonePermissionInformationDialog = false,
            microphonePermissionSnackBarText = null,
            microphonePermissionSnackBarLabel = null
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
        if (MicrophonePermission.granted.value) {
            function()
        } else {
            onEvent(RequestMicrophonePermission)
        }
    }

    fun onEvent(event: KViewModelEvent) {
        when (event) {
            is Action -> onAction(event)
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

            ConsumedMicrophonePermissionSnackBar -> _kViewState.update {
                it.copy(
                    microphonePermissionSnackBarText = null,
                    microphonePermissionSnackBarLabel = null
                )
            }

            RequestMicrophonePermissionRedirect -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (ExternalResultRequest.launch(ExternalResultRequestIntention.RequestMicrophonePermissionExternally) !is ExternalRedirectResult.Success) {
                        _kViewState.update {
                            it.copy(
                                microphonePermissionSnackBarText = MR.strings.microphonePermissionRequestFailed.stable,
                                microphonePermissionSnackBarLabel = null
                            )
                        }
                    }
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

    private fun onRequestMicrophonePermission(hasShownInformation: Boolean) {
        if (!MicrophonePermission.granted.value) {
            if (!hasShownInformation && MicrophonePermission.shouldShowInformationDialog()) {
                _kViewState.update { it.copy(isShowMicrophonePermissionInformationDialog = true) }
            } else {
                //request directly
                viewModelScope.launch(Dispatchers.IO) {
                    MicrophonePermission.request()

                    if (!MicrophonePermission.granted.value) {
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

}