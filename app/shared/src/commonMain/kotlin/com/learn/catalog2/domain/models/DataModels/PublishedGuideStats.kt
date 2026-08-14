package com.learn.catalog2.domain.models.DataModels

data class PublishedGuideStats(
    val id: String,
    val title: String,
    val downloads: Int,
    val points: Int,
    val rating: Float
) {
    // 💡 الأرباح = عدد التحميلات × سعر الكورس بالنقاط
    val revenue: Int get() = downloads * points
}

// حساب إجمالي أرباح المستخدم (Total Earnings)
fun calculateTotalEarnings(publishedGuides: List<PublishedGuideStats>): Int {
    return publishedGuides.sumOf { it.revenue }
}
