import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    jvmToolchain(11)

    iosArm64()
    iosSimulatorArm64()

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    // Modern KMP Android Library block configuration
    kotlin {
        // ... باقي إعدادات Kotlin ...

        androidLibrary {
            namespace = "com.learn.catalog2.core"
            compileSdk = libs.versions.android.compileSdk.get().toInt()
            minSdk = libs.versions.android.minSdk.get().toInt()

            withHostTest {
                isIncludeAndroidResources = true
            }
        }
        sourceSets {
            commonMain.dependencies {
                // Multiplatform dependencies
            }
            commonTest.dependencies {
                implementation(libs.kotlin.test)
            }

            // Host Unit Tests source set (replaces old androidUnitTest)
            getByName("androidHostTest").dependencies {
                implementation(libs.junit)
                // Add Robolectric or Android test dependencies here if needed
            }
        }
    }
}