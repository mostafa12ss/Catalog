// jsMain/kotlin/com/learn/catalog2/data/local/DatabaseDriverFactory.kt
package com.learn.catalog2.data.local

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        TODO("web database driver not supported yet")
    }
}