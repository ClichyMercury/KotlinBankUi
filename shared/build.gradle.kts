import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "FinSim shared module (UI + data + DI)"
        homepage = "https://example.com/finsim"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
            // Fixes the "Cannot infer a bundle ID" warning observed since Phase 1.
            binaryOption("bundleId", "com.finsim.shared")
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

            // DI — exposed because :app calls startKoin with the shared module
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)

            // ViewModel KMP + Navigation KMP (JetBrains forks of AndroidX)
            api(libs.jb.lifecycle.viewmodel)
            api(libs.jb.lifecycle.viewmodel.compose)
            api(libs.jb.lifecycle.runtime.compose)
            api(libs.jb.navigation.compose)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
