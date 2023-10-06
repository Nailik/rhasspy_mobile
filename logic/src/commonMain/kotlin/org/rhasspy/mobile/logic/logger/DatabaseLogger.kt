package org.rhasspy.mobile.logic.logger

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import database.LogElements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import okio.Path
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.logging.LogDatabase
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.database.IDriverFactory
import org.rhasspy.mobile.platformspecific.extensions.commonDatabasePath
import org.rhasspy.mobile.platformspecific.extensions.commonSave
import org.rhasspy.mobile.platformspecific.extensions.commonShare
import org.rhasspy.mobile.platformspecific.external.IExternalResultRequest
import org.rhasspy.mobile.platformspecific.readOnly

interface IDatabaseLogger {

    val flow: Flow<LogElement>

    fun shareLogFile(): Boolean
    suspend fun saveLogFile(): Boolean

    fun getPagingSource(): PagingSource<Int, LogElements>

}

class DatabaseLogger(
    private val nativeApplication: NativeApplication,
    private val externalResultRequest: IExternalResultRequest,
    driverFactory: IDriverFactory
) : IDatabaseLogger, LogWriter() {

    private val _flow = MutableSharedFlow<LogElement>()
    override val flow = _flow.readOnly

    val database = LogDatabase(driverFactory.createDriver(LogDatabase.Schema, "log.db"))

    /**
     * override log function to append text to file
     */
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        database.logElementsQueries.insert(
            id = null,
            time = Clock.System.now().toLocalDateTime(currentSystemDefault()).toString(),
            severity = severity.ordinal.toLong(),
            tag = tag,
            message = message,
            throwable = throwable?.message
        )
    }

    /**
     * share the log file
     */
    override fun shareLogFile() =
        Path.commonDatabasePath(nativeApplication, "log.db")
            .commonShare(nativeApplication, externalResultRequest)

    /**
     * save log to external file
     */
    override suspend fun saveLogFile() =
        Path.commonDatabasePath(nativeApplication, "log.db")
            .commonSave(
                nativeApplication,
                externalResultRequest,
                "rhasspy_logdatabase_${Clock.System.now().toLocalDateTime(TimeZone.UTC)}.db",
                "application/*"
            )

    @Suppress("TYPE_MISMATCH")
    override fun getPagingSource(): PagingSource<Int, LogElements> {
        return QueryPagingSource(
            database.logElementsQueries.countLogElements(),
            database.logElementsQueries,
            Dispatchers.IO,
            database.logElementsQueries::logElements,
        )
    }

}