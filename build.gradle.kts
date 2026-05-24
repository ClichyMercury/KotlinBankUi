// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // Force JavaPoet 1.13+ so Hilt's aggregateDeps task sees the newer API
        // (workaround for: java.lang.String com.squareup.javapoet.ClassName.canonicalName())
        classpath("com.squareup:javapoet:1.13.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
