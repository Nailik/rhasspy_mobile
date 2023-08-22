package org.rhasspy.mobile.settings.settingsmigration

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

object InitialSchema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
        get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
        driver.execute(
            null, """
          |CREATE TABLE settingsBooleanValues (
          |    id TEXT PRIMARY KEY,
          |    value INTEGER NOT NULL CHECK (value IN (0, 1)),
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE settingsIds (
          |  id TEXT PRIMARY KEY NOT NULL
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE settingsLongNullableValues (
          |    id TEXT PRIMARY KEY,
          |    value INTEGER,
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE settingsLongValues (
          |    id TEXT PRIMARY KEY,
          |    value INTEGER NOT NULL,
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE porcupineKeywordValues (
          |    id TEXT NOT NULL,
          |    value TEXT NOT NULL,
          |    enabled INTEGER NOT NULL CHECK (enabled IN (0, 1)),
          |    sensitivity REAL NOT NULL CHECK (sensitivity >= 0 AND sensitivity <= 1),
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE,
          |    PRIMARY KEY (id, value)
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE stringSetValues (
          |    key INTEGER PRIMARY KEY,
          |    id TEXT,
          |    value TEXT NOT NULL,
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE settingsStringNullableValues (
          |    id TEXT PRIMARY KEY,
          |    value TEXT,
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0
        )
        driver.execute(
            null, """
          |CREATE TABLE settingsStringValues (
          |    id TEXT PRIMARY KEY,
          |    value TEXT NOT NULL,
          |    FOREIGN KEY (id) REFERENCES settingsIds(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0
        )
        return QueryResult.Unit
    }

    override fun migrate(
        driver: SqlDriver,
        oldVersion: Long,
        newVersion: Long,
        vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit

}