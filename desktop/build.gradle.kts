import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(ProjectConfig.JVM_VERSION)

    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(projects.shared)
            implementation(compose.desktop.currentOs)
            implementation(libs.jna)
            implementation(libs.jna.platform)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "Main_desktopKt"

        buildTypes.release.proguard {
            configurationFiles.from("proguard-rules-jvm.pro")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = ProjectConfig.APP_NAME
            packageVersion = ProjectConfig.VERSION_NAME
            copyright = "Copyright © 2024-2026 YuKongA"
            linux {
                iconFile = file("src/desktopMain/resources/linux/Icon.png")
            }
            macOS {
                bundleID = ProjectConfig.PACKAGE_NAME
                jvmArgs("-Dapple.awt.application.appearance=system")
                iconFile = file("src/desktopMain/resources/macos/Icon.icns")
            }
            windows {
                dirChooser = true
                perUserInstall = true
                iconFile = file("src/desktopMain/resources/windows/Icon.ico")
            }
        }
    }
}
