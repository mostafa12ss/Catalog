package com.learn.catalog2.di

import com.learn.catalog2.data.local.DatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    // الـ Web مش بياخد Context
    single { DatabaseDriverFactory() }
}