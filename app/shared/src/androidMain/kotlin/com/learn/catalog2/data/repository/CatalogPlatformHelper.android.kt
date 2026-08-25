package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.coroutines.flow.Flow


actual fun getPlatformCatalogsFlow(
    localQueryFlow: () -> Flow<List<Course>>
): Flow<List<Course>> {
    // يستمر الأندرويد والديسكتب في استخدام الـ SQLDelight الاصلي
    return localQueryFlow()
}