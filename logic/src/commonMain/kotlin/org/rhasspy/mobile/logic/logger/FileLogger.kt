package org.rhasspy.mobile.logic.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.Path
import okio.buffer
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.extensions.*
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.AppSetting

interface IFileLogger {

    val flow: Flow<LogElement>

    fun getLines(): ImmutableList<LogElement>
    fun shareLogFile(): Boolean
    suspend fun saveLogFile(): Boolean

}

internal class FileLogger(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: IExternalResultRequest
) : IFileLogger, LogWriter() {
    private val logger = Logger.withTag("FileLogger")

    //create new file when logfile is 2 MB
    private val file = Path.commonInternalPath(nativeApplication, "logfile.json")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _flow = MutableSharedFlow<LogElement>()
    override val flow = _flow.readOnly

    init {
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)
    }

    /**
     * override log function to append text to file
     */
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val element = LogElement(
            Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(),
            severity,
            tag,
            message,
            throwable?.message
        )
        file.commonReadWrite().appendingSink().buffer().writeUtf8("\n,${Json.encodeToString(element)}").flush()
        coroutineScope.launch {
            _flow.emit(element)
        }
    }

    /**
     * read all lines from file
     */
    override fun getLines(): ImmutableList<LogElement> {
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
    override fun shareLogFile() = file.commonShare(nativeApplication, externalResultRequest)

    /**
     * save log to external file
     */
    override suspend fun saveLogFile() = file.commonSave(
        nativeApplication,
        externalResultRequest,
        "rhasspy_logfile_${
            Clock.System.now().toLocalDateTime(TimeZone.UTC)
        }.json",
        "application/json"
    )

}