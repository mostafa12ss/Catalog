// iosArm64Main + iosSimulatorArm64Main/kotlin/com/learn/catalog2/data/local/DatabaseDriverFactory.kt
package com.learn.catalog2.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.learn.catalog2.database.CatalogDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(CatalogDatabase.Schema, "catalog.db")
    }
}