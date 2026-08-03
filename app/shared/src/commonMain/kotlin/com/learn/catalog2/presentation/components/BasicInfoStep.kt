package com.learn.catalog2.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.next
import com.learn.catalog2.domain.models.DataModels.Category
import org.jetbrains.compose.resources.stringResource

@Composable
fun BasicInfoStep(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    onNext: () -> Unit,
    categories: List<Category> // إضافة البارامتر المفقود
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Guide Title",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Hydraulic Pump Repair SOP") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium
        )
        // تمرير القائمة للمكون ليعرض التصنيفات الحقيقية
        CategoryChips(
            categories = categories,
            selectedCategory = selectedCategory,
            onSelect = onCategorySelect
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Price (pts)",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = price,
            onValueChange = onPriceChange,
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text("pts", color = MaterialTheme.colorScheme.primary) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && selectedCategory != null && price.isNotBlank()
        ) {
            Text(stringResource(Res.string.next))
        }
    }
}
