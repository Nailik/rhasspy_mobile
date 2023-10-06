package org.rhasspy.mobile.logic

import org.koin.core.component.KoinComponent

internal interface IDomain : KoinComponent {
    fun dispose()

}