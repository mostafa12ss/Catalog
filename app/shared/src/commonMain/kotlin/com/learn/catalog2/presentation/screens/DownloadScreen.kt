package com.learn.catalog2.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog2.app.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private val BackgroundDark = Color(0xFF0D131E)
private val CardBackground = Color(0xFF161F2E)
private val PrimaryAccent = Color(0xFF64FFDA)
private val TextWhite = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF8892B0)

private data class PlatformInfo(
    val title: String,
    val subtitle: String,
    val version: String,
    val fileSize: String,
    val updatedDate: String,
    val iconPainter: Painter? = null,
    val buttonText: String,
    val downloadUrl: String,
    val isRecommended: Boolean = false
)

@Composable
fun DownloadScreen(
    onNavigateToWebApp: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val platforms = listOf(
        PlatformInfo(
            title = "Android",
            subtitle = "apk (ARM64)",
            version = "v1.0.0",
            fileSize = "42 MB",
            updatedDate = "Aug 21, 2026",
            iconPainter = painterResource(Res.drawable.baseline_android_24),
            buttonText = "Download APK",
            downloadUrl = "https://YOUR_SUPABASE_URL/storage/v1/object/public/apk/catalog2-latest.apk",
            isRecommended = true
        ),
        PlatformInfo(
            title = "iOS",
            subtitle = "",
            version = "v1.0.0",
            fileSize = "38 MB",
            updatedDate = "Aug 21, 2026",
            iconPainter = null,
            buttonText = "View on App Store",
            downloadUrl = "https://apps.apple.com/app/your-app-id"
        ),
        PlatformInfo(
            title = "macOS",
            subtitle = "dmg installer",
            version = "v1.0.0",
            fileSize = "118 MB",
            updatedDate = "Aug 21, 2026",
            iconPainter = painterResource(Res.drawable.baseline_desktop_mac_24),
            buttonText = "Download for macOS",
            downloadUrl = "https://YOUR_SUPABASE_URL/storage/v1/object/public/desktop/catalog2-mac.dmg"
        ),
        PlatformInfo(
            title = "Windows",
            subtitle = "exe installer",
            version = "v1.0.0",
            fileSize = "95 MB",
            updatedDate = "Aug 21, 2026",
            iconPainter = painterResource(Res.drawable.baseline_desktop_mac_24),
            buttonText = "Download for Windows",
            downloadUrl = "https://YOUR_SUPABASE_URL/storage/v1/object/public/desktop/catalog2-win.exe"
        ),
        PlatformInfo(
            title = "Linux",
            subtitle = "AppImage / deb",
            version = "v1.0.0",
            fileSize = "88 MB",
            updatedDate = "Aug 21, 2026",
            iconPainter = painterResource(Res.drawable.baseline_desktop_mac_24),
            buttonText = "Download for Linux",
            downloadUrl = "https://YOUR_SUPABASE_URL/storage/v1/object/public/desktop/catalog2-linux.AppImage"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CardBackground, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.outline_account_circle_24),
                        contentDescription = "Profile",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Catalog",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            // Title
            Text(
                text = "Download Catalog",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose the version that matches your device.",
                fontSize = 14.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(1.dp, PrimaryAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Text(
                    text = "Android detected · Recommended for you",
                    color = PrimaryAccent,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Platform Cards Grid
            Column(
                modifier = Modifier.widthIn(max = 850.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                platforms.chunked(2).forEach { rowPlatforms ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowPlatforms.forEach { platform ->
                            PlatformCard(
                                modifier = Modifier.weight(1f),
                                title = platform.title,
                                subtitle = platform.subtitle,
                                version = platform.version,
                                fileSize = platform.fileSize,
                                updatedDate = platform.updatedDate,
                                iconPainter = platform.iconPainter,
                                downloadIconPainter = painterResource(Res.drawable.outline_download_2_24),
                                isRecommended = platform.isRecommended,
                                buttonText = platform.buttonText,
                                onDownloadClick = { uriHandler.openUri(platform.downloadUrl) }
                            )
                        }
                        if (rowPlatforms.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Web App Banner
            Surface(
                modifier = Modifier
                    .widthIn(max = 850.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CardBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onNavigateToWebApp,
                        colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, PrimaryAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_north_west_24),
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Open Web App", color = PrimaryAccent, fontSize = 14.sp)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Don't want to install Catalog?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Access all features directly from your browser without downloading.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(BackgroundDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_north_west_24),
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PlatformCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    version: String,
    fileSize: String,
    updatedDate: String,
    iconPainter: Painter?,
    downloadIconPainter: Painter,
    isRecommended: Boolean = false,
    buttonText: String,
    onDownloadClick: () -> Unit
) {
    Surface(
        modifier = modifier.then(
            if (isRecommended) Modifier.border(1.dp, PrimaryAccent, RoundedCornerShape(16.dp)) else Modifier
        ),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (isRecommended) {
                    Surface(
                        color = PrimaryAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "RECOMMENDED",
                            color = PrimaryAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = title,
                            fontSize = if (iconPainter == null) 26.sp else 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    iconPainter?.let {
                        Icon(
                            painter = it,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            DetailRow("Version", version)
            DetailRow("File Size", fileSize)
            DetailRow("Updated", updatedDate)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDownloadClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecommended) PrimaryAccent else BackgroundDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        color = if (isRecommended) BackgroundDark else TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        painter = downloadIconPainter,
                        contentDescription = null,
                        tint = if (isRecommended) BackgroundDark else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = value, fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Medium)
        Text(text = label, fontSize = 12.sp, color = TextMuted)
    }
}