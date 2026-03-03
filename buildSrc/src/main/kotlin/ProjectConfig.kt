object ProjectConfig {
    const val JVM_VERSION = 21
    const val APP_NAME = "FontWeightTest"
    const val PACKAGE_NAME = "top.yukonga.fontWeightTest"
    const val VERSION_NAME = "1.6.4"
    val VERSION_CODE = getGitVersionCode() + 100

    object Android {
        const val TARGET_SDK = 36
        const val MIN_SDK = 26
        const val COMPILE_SDK = 36
        const val BUILD_TOOLS_VERSION = "36.1.0"
    }

    private fun getGitVersionCode(): Int {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD").start()
        return process.inputStream.bufferedReader().use { it.readText().trim().toInt() }
    }
}
