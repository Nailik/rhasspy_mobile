package org.rhasspy.mobile.logic.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.Path
import okio.buffer
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.extensions.commonDecodeLogList
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.platformspecific.extensions.commonSave
import org.rhasspy.mobile.platformspecific.extensions.commonShare
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toImmutableList

object FileLogger : LogWriter(), KoinComponent {
    private val logger = Logger.withTag("FileLogger")

    //create new file when logfile is 2 MB
    private val file = Path.commonInternalPath(get(), "logfile.txt")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _flow = MutableSharedFlow<LogElement>()
    val flow = _flow.readOnly

    init {
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)
    }

    /**
     * override log function to append text to file
     */
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        coroutineScope.launch {
            val element = LogElement(
                Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(),
                severity,
                tag,
                message,
                throwable?.message
            )
            file.commonReadWrite().appendingSink().buffer().writeUtf8(",${Json.encodeToString(element)}").also {
                it.flush()
            }
            _flow.emit(element)
        }
    }

    /**
     * read all lines from file
     */
    fun getLines(): ImmutableList<LogElement> {
        return try {
            file.commonDecodeLogList<Array<LogElement>>().toImmutableList()
        } catch (exception: Exception) {
            logger.e(exception) { "could not read log file" }
            persistentListOf()
        }
    }

    /**
     * share the log file
     */
    fun shareLogFile() = file.commonShare(get())

    /**
     * save log to external file
     */
    fun saveLogFile() = file.commonSave(
        get(),
        "rhasspy_logfile_${
            Clock.System.now().toLocalDateTime(TimeZone.UTC)
        }.txt",
        "text/txt"
    )

}