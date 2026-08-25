import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.app.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.learn.catalog2.MainKt"
        jvmArgs += listOf("-Dskiko.renderApi=OPENGL")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Catalog"
            packageVersion = "1.0.0"

            // ⚡ إعدادات الأيقونات والهوية لكل نظام
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                bundleID = "com.learn.catalog"
            }
            windows {
//                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                shortcut = true
                menuGroup = "Catalog"
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
                shortcut = true
            }
        }
    }
}