package com.learn.catalog2.domain.models.DataModels

data class Category(
    val id: String,
    val name: String,
    val count: Int=0,
    val name1: String = name// الحفاظ على الخاصية القديمة لتجنب كسر الكود
)
