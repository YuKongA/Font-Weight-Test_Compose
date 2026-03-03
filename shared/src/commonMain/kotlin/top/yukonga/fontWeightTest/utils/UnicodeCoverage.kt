package top.yukonga.fontWeightTest.utils

import fontweighttest.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource

private const val PROGRESS_INTERVAL = 2048
private const val UNIHAN_IRG_SOURCES_PATH = "files/Unihan_IRGSources.txt"
private const val SCRIPTS_PATH = "files/Scripts.txt"
private const val SCRIPT_EXTENSIONS_PATH = "files/ScriptExtensions.txt"
private const val UNICODE_DATA_PATH = "files/UnicodeData.txt"
private const val UNICODE_BLOCKS_PATH = "files/Blocks.txt"
private const val UNICODE_DRAFT_UNIHAN_ZIP_URL = "https://unicode.org/Public/draft/ucd/Unihan.zip"
private const val UNICODE_VERSIONED_UNIHAN_ZIP_URL = "https://unicode.org/Public/17.0.0/ucd/Unihan.zip"
private const val UNICODE_DRAFT_UCD_BASE_URL = "https://unicode.org/Public/draft/ucd"
private const val UNICODE_VERSIONED_UCD_BASE_URL = "https://unicode.org/Public/17.0.0/ucd"
private const val UNKNOWN_BLOCK_NAME = "Unknown"

private val unicodeFilterCategories = setOf("Cc", "Cf", "Co", "Zs", "Zl", "Zp", "Mn", "Cs", "Cn")

private var cachedHanScriptCodePoints: IntArray? = null
private var cachedUnicodeDataCodePoints: IntArray? = null
private var cachedUnicodeBlocks: List<UnicodeBlock>? = null
private val hanScriptsCacheMutex = Mutex()
private val unicodeDataCacheMutex = Mutex()
private val unicodeBlocksCacheMutex = Mutex()

enum class UnicodeCoverageMode {
    UNIHAN,
    UNICODE
}

data class UnicodeCoverageProgress(
    val processedCount: Int,
    val supportedCount: Int,
    val totalCount: Int
) {
    val percentage: Double
        get() = if (processedCount == 0) 0.0 else supportedCount.toDouble() / processedCount * 100
}

data class UnicodeCoverageResult(
    val mode: UnicodeCoverageMode,
    val processedCount: Int,
    val supportedCount: Int,
    val totalCount: Int,
    val durationMillis: Long,
    val blockResults: List<UnicodeCoverageBlockResult>
) {
    val percentage: Double
        get() = if (processedCount == 0) 0.0 else supportedCount.toDouble() / processedCount * 100

    val grade: String
        get() = gradeFromScore(percentage)
}

data class UnicodeCoverageBlockResult(
    val blockName: String,
    val supportedCount: Int,
    val totalCount: Int
) {
    val percentage: Double
        get() = if (totalCount == 0) 0.0 else supportedCount.toDouble() / totalCount * 100

    val grade: String
        get() = gradeFromScore(percentage)
}

expect object UnicodeGlyphSupport {
    fun hasGlyph(codePoint: Int): Boolean
}

suspend fun measureUnicodeCoverage(
    mode: UnicodeCoverageMode,
    onProgress: (UnicodeCoverageProgress) -> Unit
): UnicodeCoverageResult = withContext(Dispatchers.Default) {
    val codePoints = loadCodePoints(mode)
    val blocks = loadUnicodeBlocks()
    val blockStats = LinkedHashMap<String, MutableBlockStatistics>()
    var processed = 0
    var supported = 0
    val total = codePoints.size
    val timeMark = TimeSource.Monotonic.markNow()
    var blockIndex = 0

    for (codePoint in codePoints) {
        while (blockIndex < blocks.size && codePoint > blocks[blockIndex].end) {
            blockIndex++
        }
        val blockName = if (blockIndex < blocks.size && codePoint >= blocks[blockIndex].start) {
            blocks[blockIndex].name
        } else {
            UNKNOWN_BLOCK_NAME
        }
        val blockStat = blockStats.getOrPut(blockName) { MutableBlockStatistics() }

        val hasGlyph = UnicodeGlyphSupport.hasGlyph(codePoint)
        if (hasGlyph) {
            supported++
            blockStat.supported++
        }
        blockStat.total++
        processed++
        if (processed % PROGRESS_INTERVAL == 0) {
            onProgress(
                UnicodeCoverageProgress(
                    processedCount = processed,
                    supportedCount = supported,
                    totalCount = total
                )
            )
        }
    }

    val result = UnicodeCoverageResult(
        mode = mode,
        processedCount = processed,
        supportedCount = supported,
        totalCount = total,
        durationMillis = timeMark.elapsedNow().inWholeMilliseconds,
        blockResults = blockStats.map { (name, value) ->
            UnicodeCoverageBlockResult(
                blockName = name,
                supportedCount = value.supported,
                totalCount = value.total
            )
        }
    )
    onProgress(
        UnicodeCoverageProgress(
            processedCount = result.processedCount,
            supportedCount = result.supportedCount,
            totalCount = result.totalCount
        )
    )
    result
}

private suspend fun loadCodePoints(mode: UnicodeCoverageMode): IntArray {
    return when (mode) {
        UnicodeCoverageMode.UNIHAN -> loadHanScriptCodePoints()
        UnicodeCoverageMode.UNICODE -> loadUnicodeDataCodePoints()
    }
}

private suspend fun loadHanScriptCodePoints(): IntArray {
    cachedHanScriptCodePoints?.let { return it }
    hanScriptsCacheMutex.withLock {
        cachedHanScriptCodePoints?.let { return it }
        val scriptsTextResult = runCatching {
            Res.readBytes(SCRIPTS_PATH).decodeToString()
        }
        val scriptExtensionsTextResult = runCatching {
            Res.readBytes(SCRIPT_EXTENSIONS_PATH).decodeToString()
        }

        val scriptsText = scriptsTextResult.getOrNull()
        val scriptExtensionsText = scriptExtensionsTextResult.getOrNull()
        if (scriptsText == null || scriptExtensionsText == null) {
            val fallbackText = runCatching {
                Res.readBytes(UNIHAN_IRG_SOURCES_PATH).decodeToString()
            }.getOrElse {
                throw IllegalStateException(
                    "Missing Han source files. Prefer $SCRIPTS_PATH and $SCRIPT_EXTENSIONS_PATH (download from $UNICODE_DRAFT_UCD_BASE_URL with fallback $UNICODE_VERSIONED_UCD_BASE_URL). Fallback is $UNIHAN_IRG_SOURCES_PATH (from $UNICODE_DRAFT_UNIHAN_ZIP_URL, fallback $UNICODE_VERSIONED_UNIHAN_ZIP_URL)."
                )
            }
            val fallbackSet = HashSet<Int>(100_000)
            fallbackText.lineSequence().forEach { line ->
                if (line.isBlank() || line.startsWith("#")) return@forEach
                val parts = line.split('\t')
                if (parts.size < 2 || !parts[0].startsWith("U+") || !parts[1].startsWith("kIRG_")) return@forEach
                val cp = parts[0].removePrefix("U+").toIntOrNull(16) ?: return@forEach
                fallbackSet.add(cp)
            }
            val fallbackResult = fallbackSet.toIntArray().sortedArray()
            cachedHanScriptCodePoints = fallbackResult
            return fallbackResult
        }

        val ranges = ArrayList<IntRange>()
        scriptsText.lineSequence().forEach { line ->
            val content = line.substringBefore('#').trim()
            if (content.isEmpty()) return@forEach
            val parts = content.split(';')
            if (parts.size < 2) return@forEach
            if (parts[1].trim() != "Han") return@forEach
            parseCodeRange(parts[0].trim())?.let { ranges.add(it) }
        }
        scriptExtensionsText.lineSequence().forEach { line ->
            val content = line.substringBefore('#').trim()
            if (content.isEmpty()) return@forEach
            val parts = content.split(';')
            if (parts.size < 2) return@forEach
            val scripts = parts[1].trim().split(' ')
            if (!scripts.contains("Hani")) return@forEach
            parseCodeRange(parts[0].trim())?.let { ranges.add(it) }
        }

        val mergedRanges = mergeRanges(ranges)
        val resultList = ArrayList<Int>(110_000)
        mergedRanges.forEach { range ->
            for (cp in range.first..range.last) {
                resultList.add(cp)
            }
        }
        val result = resultList.toIntArray()
        cachedHanScriptCodePoints = result
        return result
    }
}

private suspend fun loadUnicodeDataCodePoints(): IntArray {
    cachedUnicodeDataCodePoints?.let { return it }
    unicodeDataCacheMutex.withLock {
        cachedUnicodeDataCodePoints?.let { return it }
        val text = runCatching {
            Res.readBytes(UNICODE_DATA_PATH).decodeToString()
        }.getOrElse {
            throw IllegalStateException(
                "Missing $UNICODE_DATA_PATH. Run ./gradlew :shared:downloadUnicodeCoverageData -Poverwrite or manually download UnicodeData.txt from $UNICODE_DRAFT_UCD_BASE_URL (fallback: $UNICODE_VERSIONED_UCD_BASE_URL) to shared/src/commonMain/composeResources/files/."
            )
        }
        val list = ArrayList<Int>(300_000)
        text.lineSequence().forEach { line ->
            if (line.isBlank() || line.startsWith("#")) {
                return@forEach
            }
            val fields = line.split(';')
            if (fields.size < 3) return@forEach
            val codePointHex = fields[0].trim()
            val name = fields[1].trim()
            val category = fields[2].trim()
            if (unicodeFilterCategories.contains(category)) return@forEach
            val codePoint = codePointHex.toIntOrNull(16) ?: return@forEach
            if (name.startsWith("<") && name.endsWith("First>")) {
                list.add(-codePoint - 1)
                return@forEach
            }
            if (name.startsWith("<") && name.endsWith("Last>")) {
                val startMarker = list.removeLastOrNull() ?: return@forEach
                if (startMarker >= 0) return@forEach
                val start = -startMarker - 1
                for (cp in start..codePoint) {
                    list.add(cp)
                }
                return@forEach
            }
            list.add(codePoint)
        }
        val result = list.distinct().sorted().toIntArray()
        cachedUnicodeDataCodePoints = result
        return result
    }
}

private suspend fun loadUnicodeBlocks(): List<UnicodeBlock> {
    cachedUnicodeBlocks?.let { return it }
    unicodeBlocksCacheMutex.withLock {
        cachedUnicodeBlocks?.let { return it }
        val text = runCatching {
            Res.readBytes(UNICODE_BLOCKS_PATH).decodeToString()
        }.getOrElse {
            throw IllegalStateException(
                "Missing $UNICODE_BLOCKS_PATH. Run ./gradlew :shared:downloadUnicodeCoverageData -Poverwrite or manually download Blocks.txt from $UNICODE_DRAFT_UCD_BASE_URL (fallback: $UNICODE_VERSIONED_UCD_BASE_URL) to shared/src/commonMain/composeResources/files/."
            )
        }
        val blocks = ArrayList<UnicodeBlock>()
        text.lineSequence().forEach { line ->
            val content = line.substringBefore('#').trim()
            if (content.isEmpty()) {
                return@forEach
            }
            val parts = content.split(';')
            if (parts.size < 2) {
                return@forEach
            }
            val rangePart = parts[0].trim()
            val name = parts[1].trim()
            val bounds = rangePart.split("..")
            if (bounds.size != 2) {
                return@forEach
            }
            val start = bounds[0].toIntOrNull(16) ?: return@forEach
            val end = bounds[1].toIntOrNull(16) ?: return@forEach
            blocks.add(UnicodeBlock(start = start, end = end, name = name))
        }
        val result = blocks.sortedBy { it.start }
        cachedUnicodeBlocks = result
        return result
    }
}

private data class UnicodeBlock(
    val start: Int,
    val end: Int,
    val name: String
)

private data class MutableBlockStatistics(
    var supported: Int = 0,
    var total: Int = 0
)

private fun parseCodeRange(rangeText: String): IntRange? {
    val bounds = rangeText.split("..")
    return if (bounds.size == 2) {
        val start = bounds[0].toIntOrNull(16) ?: return null
        val end = bounds[1].toIntOrNull(16) ?: return null
        start..end
    } else {
        val value = rangeText.toIntOrNull(16) ?: return null
        value..value
    }
}

private fun mergeRanges(ranges: List<IntRange>): List<IntRange> {
    if (ranges.isEmpty()) return emptyList()
    val sorted = ranges.sortedBy { it.first }
    val merged = ArrayList<IntRange>(sorted.size)
    var currentStart = sorted[0].first
    var currentEnd = sorted[0].last
    for (i in 1 until sorted.size) {
        val range = sorted[i]
        if (range.first <= currentEnd + 1) {
            if (range.last > currentEnd) {
                currentEnd = range.last
            }
        } else {
            merged.add(currentStart..currentEnd)
            currentStart = range.first
            currentEnd = range.last
        }
    }
    merged.add(currentStart..currentEnd)
    return merged
}

private fun gradeFromScore(score: Double): String {
    if (score >= 99.9999) return "PG"
    if (score >= 96.0) return "EX"
    if (score >= 90.0) return "A"
    if (score >= 82.0) return "B"
    if (score >= 70.0) return "C"
    if (score >= 62.0) return "D"
    if (score >= 42.0) return "E"
    return "F"
}
