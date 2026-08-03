// androidMain/kotlin/com/learn/catalog2/data/local/DatabaseDriverFactory.kt
package com.learn.catalog2.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.learn.catalog2.database.CatalogDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(CatalogDatabase.Schema, context, "catalog.db")
    }
}