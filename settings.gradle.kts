@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("com.android.settings") version ("8.11.0")
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

android {
    compileSdk = 36
    targetSdk = 36
    minSdk = 26
}

rootProject.name = "FontWeightTest"
include(":composeApp")