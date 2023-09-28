package org.rhasspy.mobile.logic

import org.koin.core.component.KoinComponent

interface IDomain : KoinComponent {
    fun dispose()

}