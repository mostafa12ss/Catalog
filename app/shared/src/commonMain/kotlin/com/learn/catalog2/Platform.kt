package com.learn.catalog2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform