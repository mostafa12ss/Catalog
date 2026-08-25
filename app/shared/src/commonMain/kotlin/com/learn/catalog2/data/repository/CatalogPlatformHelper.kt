package com.learn.catalog2.data.repository


import com.learn.catalog2.domain.models.DataModels.Course
import kotlinx.coroutines.flow.Flow

expect fun getPlatformCatalogsFlow(
    localQueryFlow: () -> Flow<List<Course>>
): Flow<List<Course>>