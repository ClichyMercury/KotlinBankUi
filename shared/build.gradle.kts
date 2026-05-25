import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            // Data layer — exposed in DTO/repo signatures, so api()
            api(libs.kotlinx.datetime)
            api(libs.ionspin.bignum)
            api(libs.ionspin.bignum.serialization)
            api(libs.kotlinx.serialization.json)

            // Network — internal to repositories
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Charts
            implementation(libs.vico.multiplatform.m3)

            // Material icons extended (last published version 1.7.3, ABI-compatible
            // with CMP 1.8.x since icons are passive resources)
            @Suppress("DEPRECATION")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.datastore.preferences)
        }

        iosMain.dependencies {
            // iOS Ktor engine — added in Phase 5; for Phase 2 the iOS framework only
            // needs to link, not run HTTP. Repos compile, ApiClient.create() will
            // need an actual when we add iOS deps in Phase 5.
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
                }
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.finsim.resources"
}

android {
    namespace = "com.finsim.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
