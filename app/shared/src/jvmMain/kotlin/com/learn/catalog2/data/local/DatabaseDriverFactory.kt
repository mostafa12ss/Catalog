package com.learn.catalog2.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.learn.catalog2.database.CatalogDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("catalog.db")
        val isNewDatabase = !dbFile.exists()

        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        // إنشاء الجداول فقط لو الداتا بيز لسة جديدة ومش موجودة
        if (isNewDatabase) {
            CatalogDatabase.Schema.create(driver)
        }

        return driver
    }
}