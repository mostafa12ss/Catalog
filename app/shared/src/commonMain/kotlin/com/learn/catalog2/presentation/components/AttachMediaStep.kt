package com.learn.catalog2.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.Res
import catalog2.app.shared.generated.resources.back
import catalog2.app.shared.generated.resources.save_publish
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import org.jetbrains.compose.resources.stringResource

@Composable
fun AttachMediaStep(
    selectedFileTypes: Set<String>,
    onFileTypeToggle: (String) -> Unit,
    onBack: () -> Unit,
    onFileSelected: (PlatformFile) -> Unit, // 💡 تمرير الملف للـ ViewModel مباشرة
    isPublishing: Boolean,                 // 💡 حالة النشر لمنع التكرار
    onPublish: () -> Unit
) {
    var selectedFile by remember { mutableStateOf<PlatformFile?>(null) }

    // دالة مساعدة لحفظ الملف محلياً وإرساله للـ ViewModel
    val handleFileSelection: (PlatformFile?) -> Unit = { file ->
        if (file != null) {
            selectedFile = file
            onFileSelected(file) // 👈 إرسال الملف للـ ViewModel فوراً
        }
    }

    val videoPickerLauncher = rememberFilePickerLauncher(
        type = PickerType.Video,
        onResult = handleFileSelection
    )

    val pdfPickerLauncher = rememberFilePickerLauncher(
        type = PickerType.File(listOf("pdf")),
        onResult = handleFileSelection
    )

    val wordPickerLauncher = rememberFilePickerLauncher(
        type = PickerType.File(listOf("doc", "docx")),
        onResult = handleFileSelection
    )

    val zipPickerLauncher = rememberFilePickerLauncher(
        type = PickerType.File(listOf("zip", "rar", "7z")),
        onResult = handleFileSelection
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Attach files to your guide",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select file types and pick your file from device",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        FileTypeSelectableCard(
            title = "Video File",
            subtitle = ".mp4 / .mov / .mkv",
            icon = "🎥",
            isSelected = selectedFileTypes.contains("video"),
            onToggle = { onFileTypeToggle("video") },
            onPickFile = { videoPickerLauncher.launch() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FileTypeSelectableCard(
            title = "PDF Document",
            subtitle = ".pdf",
            icon = "📄",
            isSelected = selectedFileTypes.contains("pdf"),
            onToggle = { onFileTypeToggle("pdf") },
            onPickFile = { pdfPickerLauncher.launch() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FileTypeSelectableCard(
            title = "Word Document",
            subtitle = ".doc / .docx",
            icon = "📝",
            isSelected = selectedFileTypes.contains("word"),
            onToggle = { onFileTypeToggle("word") },
            onPickFile = { wordPickerLauncher.launch() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FileTypeSelectableCard(
            title = "Compressed ZIP",
            subtitle = ".zip / .rar / .7z",
            icon = "📦",
            isSelected = selectedFileTypes.contains("zip"),
            onToggle = { onFileTypeToggle("zip") },
            onPickFile = { zipPickerLauncher.launch() }
        )

        selectedFile?.let { file ->
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📂 Selected File: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(file.name, fontSize = 13.sp, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                enabled = !isPublishing
            ) {
                Text(stringResource(Res.string.back))
            }

            Button(
                onClick = onPublish,
                modifier = Modifier.weight(1f),
                enabled = selectedFileTypes.isNotEmpty() && selectedFile != null && !isPublishing
            ) {
                if (isPublishing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(Res.string.save_publish))
                }
            }
        }
    }
}

@Composable
private fun FileTypeSelectableCard(
    title: String,
    subtitle: String,
    icon: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onPickFile: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (isSelected) {
                Button(
                    onClick = onPickFile,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Choose", fontSize = 12.sp)
                }
            }
        }
    }
}