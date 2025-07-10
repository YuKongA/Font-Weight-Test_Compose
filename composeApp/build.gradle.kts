@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractNativeMacApplicationPackageAppDirTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.KonanTarget
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
kotlin {
    androidTarget()

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    listOf(
        macosX64(),
        macosArm64(),
    ).forEach {
        it.binaries.executable {
            entryPoint = "main"
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
        nativeApplication {
            targets(kotlin.targets.getByName("macosArm64"), kotlin.targets.getByName("macosX64"))
            distributions {
                targetFormats(TargetFormat.Dmg)
                packageName = appName
                packageVersion = verName
                description = "Font Weight Test"
                copyright = "Copyright © 2024-2025 YuKongA"
                macOS {
                    bundleID = pkgName
                    //iconFile = file("src/macosMain/resources/FontWeightTest.icns")
                }
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

afterEvaluate {
    project.extensions.getByType<KotlinMultiplatformExtension>().targets
        .withType<KotlinNativeTarget>()
        .filter { it.konanTarget == KonanTarget.MACOS_ARM64 || it.konanTarget == KonanTarget.MACOS_X64 }
        .forEach { target ->
            val targetName = target.targetName.uppercaseFirstChar()
            val buildTypes = mapOf(
                NativeBuildType.RELEASE to target.binaries.getExecutable(NativeBuildType.RELEASE),
                NativeBuildType.DEBUG to target.binaries.getExecutable(NativeBuildType.DEBUG)
            )
            buildTypes.forEach { (buildType, executable) ->
                val buildTypeName = buildType.name.lowercase().uppercaseFirstChar()
                target.binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Executable>()
                    .filter { it.buildType == buildType }
                    .forEach {
                        val taskName = "copy${buildTypeName}ComposeResourcesFor${targetName}"
                        val copyTask = tasks.register<Copy>(taskName) {
                            from({
                                (executable.compilation.associatedCompilations + executable.compilation).flatMap { compilation ->
                                    compilation.allKotlinSourceSets.map { it -> it.resources }
                                }
                            })
                            into(executable.outputDirectory.resolve("compose-resources"))
                            exclude("*.icns")
                        }
                        it.linkTaskProvider.dependsOn(copyTask)
                    }
            }
        }
}

tasks.withType<AbstractNativeMacApplicationPackageAppDirTask>().configureEach {
    doLast {
        val packageName = packageName.get()
        val destinationDir = outputs.files.singleFile
        val appDir = destinationDir.resolve("$packageName.app")
        val resourcesDir = appDir.resolve("Contents/Resources")
        val currentMacosTarget = kotlin.targets.withType<KotlinNativeTarget>()
            .find { it.konanTarget == KonanTarget.MACOS_ARM64 || it.konanTarget == KonanTarget.MACOS_X64 }?.targetName
        val composeResourcesDir = project.rootDir
            .resolve("composeApp/build/bin/$currentMacosTarget/releaseExecutable/compose-resources")
        if (composeResourcesDir.exists()) {
            project.copy {
                from(composeResourcesDir)
                into(resourcesDir.resolve("compose-resources"))
            }
        }
    }
}
