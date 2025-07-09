@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
}

val appName = "FontWeightTest"
val pkgName = "top.yukonga.fontWeightTest"
val verName = "1.6.0"
val verCode = getVersionCode()
val generatedSrcDir = layout.buildDirectory.dir("generated").get().asFile.resolve("fontWeightTest")
val xcf = XCFramework(appName + "Framework")
kotlin {
    androidTarget()

    jvm("desktop")

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = appName + "Framework"
            isStatic = true
            freeCompilerArgs += "-Xbinary=bundleId=$pkgName.framework"
            xcf.add(this)
        }
    }

    sourceSets {
        val desktopMain by getting
        val commonMain by getting {
            kotlin.srcDir(generatedSrcDir.resolve("kotlin").absolutePath)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.miuix)
            implementation(libs.haze)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.jna)
            implementation(libs.jna.platform)
        }
    }


}

android {
    namespace = pkgName
    defaultConfig {
        applicationId = pkgName
        versionCode = verCode
        versionName = verName
    }
    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath != null) {
        signingConfigs {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules-android.pro")
            if (keystorePath != null) signingConfig = signingConfigs.getByName("release")
        }
        debug {
            if (keystorePath != null) signingConfig = signingConfigs.getByName("release")
        }
    }
    dependenciesInfo.includeInApk = false
    kotlin.jvmToolchain(21)
    packaging {
        applicationVariants.all {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName = "$appName-v$versionName($versionCode)-$name.apk"
            }
        }
        resources.excludes += "**"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            optimize = false
            configurationFiles.from("proguard-rules-jvm.pro")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = appName
            packageVersion = verName
            description = "Font Weight Test"
            copyright = "Copyright © 2024-2025 YuKongA"

            linux {
                //iconFile = file("src/desktopMain/resources/linux/Icon.png")
            }
            macOS {
                bundleID = pkgName
                jvmArgs("-Dapple.awt.application.appearance=system")
                //iconFile = file("src/desktopMain/resources/macos/Icon.icns")
            }
            windows {
                dirChooser = true
                perUserInstall = true
                //iconFile = file("src/desktopMain/resources/windows/Icon.ico")
            }
        }
    }
}

fun getGitCommitCount(): Int {
    val process = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", "HEAD"))
    return process.inputStream.bufferedReader().use { it.readText().trim().toInt() }
}

fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    val major = 99
    return major + commitCount
}

val generateVersionInfo by tasks.registering {
    doLast {
        val file = generatedSrcDir.resolve("kotlin/misc/VersionInfo.kt")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(
            """
            package misc

            object VersionInfo {
                const val VERSION_NAME = "$verName"
                const val VERSION_CODE = $verCode
            }
            """.trimIndent()
        )
    }
}

tasks.named("generateComposeResClass").configure {
    dependsOn(generateVersionInfo)
}
