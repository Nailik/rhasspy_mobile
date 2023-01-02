package org.rhasspy.mobile.logger

import co.touchlab.kermit.Logger

enum class LogType {

    Test;

    fun logger(): Logger {
        return Logger.withTag(this.name)
    }

}