package com.learn.catalog2.data.repository

import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.coroutines.flow.Flow

actual fun getPlatformCatalogsFlow(
    localQueryFlow: () -> Flow<List<Course>>
): Flow<List<Course>> {
    // منصة iOS تستخدم SQLDelight والقواعد المحلية بشكل طبيعي مثل أندرويد وديسكتب
    return localQueryFlow()
}