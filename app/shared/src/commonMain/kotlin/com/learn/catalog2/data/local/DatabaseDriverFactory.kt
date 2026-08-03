package com.learn.catalog2.data.local

import app.cash.sqldelight.db.SqlDriver
import com.learn.catalog2.database.CatalogDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DatabaseDriverFactory): CatalogDatabase {
    val driver = driverFactory.createDriver()

    return CatalogDatabase(
        driver = driver,
        CourseEntityAdapter = CourseEntity.Adapter(
            fileUrlsAdapter = listOfStringsAdapter // 👈 هنا مكان التمرير الصحيح
        )
    )
}