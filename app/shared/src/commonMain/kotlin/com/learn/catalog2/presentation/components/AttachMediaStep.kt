package com.learn.catalog2.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.back
import catalog2.app.shared.generated.resources.save_publish
import org.jetbrains.compose.resources.stringResource

@Composable
fun AttachMediaStep(
    selectedFileTypes: Set<String>,
    onFileTypeToggle: (String) -> Unit,
    onBack: () -> Unit,
    onPublish: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Attach files to your guide",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Select all file types you will include",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        FileTypeItem(
            title = "Video File",
            subtitle = ".mp4 / .mov",
            icon = "🎥",
            isSelected = selectedFileTypes.contains("video"),
            onClick = { onFileTypeToggle("video") }
        )

        FileTypeItem(
            title = "Document / PDF",
            subtitle = ".pdf / .docx",
            icon = "📄",
            isSelected = selectedFileTypes.contains("document"),
            onClick = { onFileTypeToggle("document") }
        )

        FileTypeItem(
            title = "Compressed ZIP",
            subtitle = ".zip / .rar",
            icon = "📦",
            isSelected = selectedFileTypes.contains("zip"),
            onClick = { onFileTypeToggle("zip") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(Res.string.back))
            }

            Button(
                onClick = onPublish,
                modifier = Modifier.weight(1f),
                enabled = selectedFileTypes.isNotEmpty()
            ) {
                Text(stringResource(Res.string.save_publish))
            }
        }
    }
}