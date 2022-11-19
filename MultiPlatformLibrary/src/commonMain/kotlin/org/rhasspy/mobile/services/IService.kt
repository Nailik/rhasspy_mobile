package org.rhasspy.mobile.services

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.rhasspy.mobile.readOnly

abstract class IService() {

    abstract val currentError: SharedFlow<ServiceError?>

}