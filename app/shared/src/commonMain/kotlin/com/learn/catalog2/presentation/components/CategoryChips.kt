package com.learn.catalog2.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.learn.catalog2.domain.models.DataModels.Category

@Composable
fun CategoryChips(
    categories: List<Category>,
    selectedCategory: String?, // يحتوي على الـ category.id المختار
    onSelect: (String) -> Unit, // يُرجع الـ category.id عند الضغط
    isLoading: Boolean = false
) {
    if (isLoading) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = 6) {
                ChipSkeleton()
            }
        }
        return
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            // المقارنة تتم باستخدام الـ ID لضمان دقة الفلترة
            val isSelected = category.id == selectedCategory

            FilterChip(
                selected = isSelected,
                onClick = { onSelect(category.id) }, // إرسال الـ ID
                label = {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ChipSkeleton() {
    Box(
        modifier = Modifier
            .size(height = 32.dp, width = 80.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    )
}

