package com.learn.catalog2.data.local

import app.cash.sqldelight.ColumnAdapter

// Boolean → Integer (0 or 1)
object BooleanAdapter : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}

// Int → Long (للأمان)
object IntAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

// Float → Double
object FloatAdapter : ColumnAdapter<Float, Double> {
    override fun decode(databaseValue: Double): Float = databaseValue.toFloat()
    override fun encode(value: Float): Double = value.toDouble()
}

// List<String> → String
val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> {
        return if (databaseValue.isEmpty()) emptyList()
        else databaseValue.split(",")
    }

    override fun encode(value: List<String>): String {
        return value.joinToString(separator = ",")
    }
}