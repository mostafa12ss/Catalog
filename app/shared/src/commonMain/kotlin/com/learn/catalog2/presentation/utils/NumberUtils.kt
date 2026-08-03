package com.learn.catalog2.presentation.utils

import kotlin.math.roundToInt

/**
 * دالة لتنسيق الأرقام مع فواصل الآلاف (شغالة KMP)
 */
fun Int.withThousandsSeparator(): String {
    val str = kotlin.math.abs(this).toString()
    val sb = StringBuilder()
    for ((index, char) in str.reversed().withIndex()) {
        if (index != 0 && index % 3 == 0) sb.append(',')
        sb.append(char)
    }
    val result = sb.reverse().toString()
    return if (this < 0) "-$result" else result
}

/**
 * دالة لتنسيق الأرقام العشرية لرقمين بعد العلامة (شغالة KMP)
 */
fun Double.toTwoDecimalString(): String {
    val rounded = (this * 100).roundToInt() / 100.0
    val whole = rounded.toInt()
    val fraction = kotlin.math.abs(((rounded - whole) * 100).roundToInt())
    val fractionStr = if (fraction < 10) "0$fraction" else "$fraction"
    return "$whole.$fractionStr"
}
