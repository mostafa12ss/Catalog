import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)}

kotlin {
    jvmToolchain(11)

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()
    js { browser() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    androidLibrary {
        namespace = "com.learn.catalog2.app.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets {
        commonMain.dependencies {

            implementation(compose.materialIconsExtended)

            api(projects.core)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Ktor + Supabase
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.serializationKotlinxJson)

            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.realtime)

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

            implementation("io.github.vinceglb:filekit-compose:0.8.0")

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            implementation("io.github.jan-tennert.supabase:storage-kt:<YOUR_SUPABASE_VERSION>")

            // Koin + Navigation
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.navigation.compose)

            // === SQLDelight Core ===
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

//            implementation("com.benasher4448:uuid:0.8.4")

            implementation(libs.compose.uiToolingPreview)        }

        androidMain.dependencies {
            implementation(libs.ktor.clientCio)
            implementation(libs.sqldelight.androidDriver)
            implementation(libs.compose.uiTooling)
        }

        val iosArm64Main by getting{
            dependencies {
                implementation(libs.ktor.clientDarwin)
                implementation(libs.sqldelight.nativeDriver)
            }
        }

        val iosSimulatorArm64Main by getting {
            dependencies {
                implementation(libs.ktor.clientDarwin)
                implementation(libs.sqldelight.nativeDriver)
            }
        }

        jvmMain.dependencies {
            implementation(libs.ktor.clientCio)
            implementation(libs.sqldelight.sqliteDriver)
        }

        jsMain.dependencies {
//            implementation(libs.wrappers.browser)
            implementation(libs.ktor.clientJs)
            // ملحوظة: مفيش sqldelight.webDriver هنا خالص - الويب online-only
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(libs.ktor.clientJs)
                // ملحوظة: مفيش sqldelight.webDriver هنا كمان
//                implementation("app.cash.sqldelight:web-worker-driver:2.0.2")
            }
        }
    }
}


sqldelight {
    databases {
        create("CatalogDatabase") {
            packageName.set("com.learn.catalog2.database")

//            generateAsync.set(true)

        }
    }
}
