@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.net.URI
import java.util.zip.ZipInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

val generatedSrcDir = layout.buildDirectory.dir("generated").get().asFile.resolve("fontWeightTest")
val unicodeVersion = "17.0.0"
val unicodeDraftBaseUrl = "https://unicode.org/Public/draft/ucd"
val unicodeBaseUrl = "https://unicode.org/Public/$unicodeVersion/ucd"
val unihanZipName = "Unihan.zip"
val unihanEntryName = "Unihan_IRGSources.txt"
val coverageResourceDir = projectDir.resolve("src/commonMain/composeResources/files")
val unihanDestFile = coverageResourceDir.resolve(unihanEntryName)
val coveragePlainFiles = listOf("Blocks.txt", "UnicodeData.txt", "Scripts.txt", "ScriptExtensions.txt")

fun extractUnihanEntry(zipFilePath: File, destinationFile: File): Boolean {
    ZipInputStream(zipFilePath.inputStream().buffered()).use { zipInputStream ->
        var zipEntry = zipInputStream.nextEntry
        while (zipEntry != null) {
            val entryName = zipEntry.name.substringAfterLast('/')
            if (entryName == unihanEntryName) {
                destinationFile.parentFile.mkdirs()
                destinationFile.outputStream().buffered().use { output ->
                    zipInputStream.copyTo(output)
                }
                return true
            }
            zipInputStream.closeEntry()
            zipEntry = zipInputStream.nextEntry
        }
    }
    return false
}

fun downloadToFile(url: String, destinationFile: File) {
    destinationFile.parentFile.mkdirs()
    URI(url).toURL().openStream().use { input ->
        destinationFile.outputStream().buffered().use { output ->
            input.copyTo(output)
        }
    }
}

kotlin {
    jvmToolchain(ProjectConfig.JVM_VERSION)

    android {
        androidResources.enable = true
        buildToolsVersion = ProjectConfig.Android.BUILD_TOOLS_VERSION
        compileSdk = ProjectConfig.Android.COMPILE_SDK
        minSdk = ProjectConfig.Android.MIN_SDK
        namespace = "${ProjectConfig.PACKAGE_NAME}.shared"
    }

    jvm("desktop")

    fun iosTargets(config: KotlinNativeTarget.() -> Unit) {
        iosArm64(config)
        iosSimulatorArm64(config)
    }
    iosTargets {
        binaries.framework {
            baseName = "shared"
            isStatic = true
            binaryOption("bundleId", ProjectConfig.PACKAGE_NAME)
            binaryOption("smallBinary", "true")
        }
    }

    macosArm64()

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generatedSrcDir.resolve("kotlin").absolutePath)
        }
        commonMain.dependencies {
            api(libs.compose.components.resources)
            implementation(libs.miuix)
            implementation(libs.haze)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
        }
    }
}

compose.resources {
    publicResClass = true
}

tasks.named("generateComposeResClass").configure {
    dependsOn(generateVersionInfo)
}

tasks.register("downloadUnicodeCoverageData") {
    group = "unicode"
    description = "Download coverage resources from draft URL with version fallback"
    doLast {
        val tempDir = layout.buildDirectory.dir("tmp/unihanDownload").get().asFile
        tempDir.mkdirs()
        val tempZipFile = tempDir.resolve(unihanZipName)

        val sourceUrls = listOf(
            unicodeDraftBaseUrl,
            unicodeBaseUrl
        )

        val overwrite = project.hasProperty("overwrite")

        if (unihanDestFile.exists() && !overwrite) {
            println("File $unihanEntryName already exists, skipping download")
        } else {
            var downloaded = false
            for (baseUrl in sourceUrls) {
                val zipUrl = "$baseUrl/$unihanZipName"
                try {
                    println("Trying to download $unihanZipName from $baseUrl ...")
                    downloadToFile(zipUrl, tempZipFile)
                    val extracted = extractUnihanEntry(tempZipFile, unihanDestFile)
                    if (!extracted) {
                        throw IllegalStateException("Cannot find $unihanEntryName in downloaded zip")
                    }
                    println("Successfully extracted $unihanEntryName from $baseUrl")
                    downloaded = true
                    break
                } catch (e: Exception) {
                    println("Failed from $baseUrl: ${e.message}")
                    if (unihanDestFile.exists() && overwrite) {
                        unihanDestFile.delete()
                    }
                } finally {
                    if (tempZipFile.exists()) {
                        tempZipFile.delete()
                    }
                }
            }
            if (!downloaded) {
                println("Failed to download $unihanEntryName from all sources, continue without it.")
            }
        }

        coveragePlainFiles.forEach { fileName ->
            val destinationFile = coverageResourceDir.resolve(fileName)
            if (destinationFile.exists() && !overwrite) {
                println("File $fileName already exists, skipping download")
                return@forEach
            }
            var downloaded = false
            for (baseUrl in sourceUrls) {
                val fileUrl = "$baseUrl/$fileName"
                try {
                    println("Trying to download $fileName from $baseUrl ...")
                    downloadToFile(fileUrl, destinationFile)
                    println("Successfully downloaded $fileName from $baseUrl")
                    downloaded = true
                    break
                } catch (e: Exception) {
                    println("Failed from $baseUrl: ${e.message}")
                    if (destinationFile.exists() && overwrite) {
                        destinationFile.delete()
                    }
                }
            }
            if (!downloaded) {
                throw GradleException(
                    "Failed to download $fileName from draft and versioned sources. Try again later or place the file manually at ${destinationFile.absolutePath}"
                )
            }
        }
    }
}

tasks.register("downloadUnihanIrgSources") {
    group = "unicode"
    description = "Compatibility alias task"
    dependsOn("downloadUnicodeCoverageData")
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
                const val VERSION_NAME = "${ProjectConfig.VERSION_NAME}"
                const val VERSION_CODE = ${ProjectConfig.VERSION_CODE}
            }
            """.trimIndent()
        )
        val iosPlist = project.rootDir.resolve("ios/iosApp/Info.plist")
        if (iosPlist.exists()) {
            val content = iosPlist.readText()
            val updatedContent = content
                .replace(
                    Regex("<key>CFBundleShortVersionString</key>\\s*<string>[^<]*</string>"),
                    "<key>CFBundleShortVersionString</key>\n\t<string>${ProjectConfig.VERSION_NAME}</string>"
                )
                .replace(
                    Regex("<key>CFBundleVersion</key>\\s*<string>[^<]*</string>"),
                    "<key>CFBundleVersion</key>\n\t<string>${ProjectConfig.VERSION_CODE}</string>"
                )
            iosPlist.writeText(updatedContent)
        }
    }
}
