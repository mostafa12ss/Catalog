package com.learn.catalog2.domain.models.DataModels

import com.learn.catalog2.data.local.CourseEntity


fun CourseEntity.toDomain(): Course {
    return Course(
        id = id,
        rank = rank.toInt(), // SQLDelight يحول INTEGER إلى Long في كوتلن
        title = title,
        subtitle = subtitle,
        author = author,
        level = level,
        points = points.toInt(),
        downloads = downloads.toInt(),
        rating = rating.toFloat(), // SQLDelight يحول REAL إلى Double
        isSaved = isSaved == 1L,   // تحويل Long/Int إلى Boolean
        isDownloaded = isDownloaded == 1L,
        fileUrls = fileUrls
    )
}

// 2. تحويل من Domain Model إلى SQLDelight Entity (للحفظ في قاعدة البيانات)
fun Course.toEntity(): CourseEntity {
    return CourseEntity(
        id = id,
        rank = rank.toLong(),
        title = title,
        subtitle = subtitle,
        author = author,
        level = level,
        points = points.toLong(),
        downloads = downloads.toLong(),
        rating = rating.toDouble(),
        isSaved = if (isSaved) 1L else 0L,
        isDownloaded = if (isDownloaded) 1L else 0L,
        fileUrls = fileUrls
    )
}