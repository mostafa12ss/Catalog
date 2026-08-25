package com.learn.catalog2.data.local

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NoOpSqlDriver()
    }
}

// 🟢 Driver وهمي متوافق تماماً مع SQLDelight 2.0+
private class NoOpSqlDriver : SqlDriver {

    override fun currentTransaction(): Transacter.Transaction? = null

    override fun newTransaction(): QueryResult<Transacter.Transaction> {
        val dummyTransaction = object : Transacter.Transaction() {
            // 🟢 إرجاع null بأمان بدلاً من Throwing Exception
            override val enclosingTransaction: Transacter.Transaction? = null

            override fun endTransaction(successful: Boolean): QueryResult<Unit> {
                return QueryResult.Value(Unit)
            }
        }
        return QueryResult.Value(dummyTransaction)
    }

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult<Long> {
        return QueryResult.Value(0L)
    }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult<R> {
        return mapper(EmptySqlCursor)
    }

    override fun addListener(vararg queryKeys: String, listener: app.cash.sqldelight.Query.Listener) {}
    override fun removeListener(vararg queryKeys: String, listener: app.cash.sqldelight.Query.Listener) {}
    override fun notifyListeners(vararg queryKeys: String) {}

    override fun close() {}
}

private object EmptySqlCursor : SqlCursor {
    override fun next(): QueryResult<Boolean> = QueryResult.Value(false)
    override fun getString(index: Int): String? = null
    override fun getLong(index: Int): Long? = null
    override fun getBytes(index: Int): ByteArray? = null
    override fun getDouble(index: Int): Double? = null
    override fun getBoolean(index: Int): Boolean? = null
}