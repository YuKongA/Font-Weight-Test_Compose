package top.yukonga.fontWeightTest.utils

import fontweighttest.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource

private const val SCRIPTS_PATH = "files/Scripts.txt"
private const val SCRIPT_EXTENSIONS_PATH = "files/ScriptExtensions.txt"
private const val UNICODE_DATA_PATH = "files/UnicodeData.txt"
private const val UNICODE_BLOCKS_PATH = "files/Blocks.txt"
private const val UNICODE_DRAFT_UCD_BASE_URL = "https://unicode.org/Public/draft/ucd"
private const val UNICODE_VERSIONED_UCD_BASE_URL = "https://unicode.org/Public/17.0.0/ucd"
private const val UNKNOWN_BLOCK_NAME = "Unknown"

private var cachedHanScriptCodePoints: IntArray? = null
private var cachedUnicodeDataCodePoints: IntArray? = null
private var cachedUnicodeBlocks: List<UnicodeBlock>? = null
private var cachedUnicodeVersionMetadata: UnicodeVersionMetadata? = null
private val hanScriptsCacheMutex = Mutex()
private val unicodeDataCacheMutex = Mutex()
private val unicodeBlocksCacheMutex = Mutex()
private val unicodeVersionMetadataCacheMutex = Mutex()

enum class UnicodeCoverageMode {
    UNIHAN,
    UNICODE
}

data class UnicodeCoverageProgress(
    val processedCount: Int,
    val supportedCount: Int,
    val totalCount: Int,
    val currentCodePoint: Int,
    val currentChunk: IntArray = IntArray(0)
) {
    val percentage: Double
        get() = if (processedCount == 0) 0.0 else supportedCount.toDouble() / processedCount * 100

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UnicodeCoverageProgress

        if (processedCount != other.processedCount) return false
        if (supportedCount != other.supportedCount) return false
        if (totalCount != other.totalCount) return false
        if (currentCodePoint != other.currentCodePoint) return false
        if (!currentChunk.contentEquals(other.currentChunk)) return false
        if (percentage != other.percentage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = processedCount
        result = 31 * result + supportedCount
        result = 31 * result + totalCount
        result = 31 * result + currentCodePoint
        result = 31 * result + currentChunk.contentHashCode()
        result = 31 * result + percentage.hashCode()
        return result
    }
}

data class UnicodeCoverageResult(
    val mode: UnicodeCoverageMode,
    val processedCount: Int,
    val supportedCount: Int,
    val totalCount: Int,
    val durationMillis: Long,
    val blockResults: List<UnicodeCoverageBlockResult>,
    val unicodeVersionMetadata: UnicodeVersionMetadata?
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
    fun hasGlyphs(codePoints: IntArray): BooleanArray
}

suspend fun measureUnicodeCoverage(
    mode: UnicodeCoverageMode,
    onProgress: (UnicodeCoverageProgress) -> Unit
): UnicodeCoverageResult = withContext(Dispatchers.Default) {
    val codePoints = loadCodePoints(mode)
    val blocks = loadUnicodeBlocks()
    val unicodeVersionMetadata = loadUnicodeVersionMetadata()
    val blockStats = LinkedHashMap<String, MutableBlockStatistics>()
    var processed = 0
    var supported = 0
    val total = codePoints.size
    val timeMark = TimeSource.Monotonic.markNow()
    var blockIndex = 0

    val chunkSize = 4096
    for (i in codePoints.indices step chunkSize) {
        val end = minOf(i + chunkSize, codePoints.size)
        val chunk = codePoints.sliceArray(i until end)
        val glyphsSupported = UnicodeGlyphSupport.hasGlyphs(chunk)

        for (j in chunk.indices) {
            val codePoint = chunk[j]
            while (blockIndex < blocks.size && codePoint > blocks[blockIndex].end) {
                blockIndex++
            }
            val blockName = if (blockIndex < blocks.size && codePoint >= blocks[blockIndex].start) {
                blocks[blockIndex].name
            } else {
                UNKNOWN_BLOCK_NAME
            }
            val blockStat = blockStats.getOrPut(blockName) { MutableBlockStatistics() }

            if (glyphsSupported[j]) {
                supported++
                blockStat.supported++
            }
            blockStat.total++
            processed++
        }
        onProgress(
            UnicodeCoverageProgress(
                processedCount = processed,
                supportedCount = supported,
                totalCount = total,
                currentCodePoint = chunk.last(),
                currentChunk = chunk
            )
        )
    }

    val result = UnicodeCoverageResult(
        mode = mode,
        processedCount = processed,
        supportedCount = supported,
        totalCount = total,
        durationMillis = timeMark.elapsedNow().inWholeMilliseconds,
        unicodeVersionMetadata = unicodeVersionMetadata,
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
            totalCount = result.totalCount,
            currentCodePoint = 0
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
        val scriptsText = runCatching {
            Res.readBytes(SCRIPTS_PATH).decodeToString()
        }.getOrElse {
            throw IllegalStateException(
                "Missing $SCRIPTS_PATH. Run ./gradlew :shared:downloadUnicodeCoverageData -Poverwrite or manually download Scripts.txt from $UNICODE_DRAFT_UCD_BASE_URL (fallback: $UNICODE_VERSIONED_UCD_BASE_URL) to shared/src/commonMain/composeResources/files/."
            )
        }
        val scriptExtensionsText = runCatching {
            Res.readBytes(SCRIPT_EXTENSIONS_PATH).decodeToString()
        }.getOrElse {
            throw IllegalStateException(
                "Missing $SCRIPT_EXTENSIONS_PATH. Run ./gradlew :shared:downloadUnicodeCoverageData -Poverwrite or manually download ScriptExtensions.txt from $UNICODE_DRAFT_UCD_BASE_URL (fallback: $UNICODE_VERSIONED_UCD_BASE_URL) to shared/src/commonMain/composeResources/files/."
            )
        }
        if (cachedUnicodeVersionMetadata == null) {
            cachedUnicodeVersionMetadata = mergeUnicodeVersionMetadata(
                parseUnicodeVersionMetadata(scriptsText, "Scripts"),
                parseUnicodeVersionMetadata(scriptExtensionsText, "ScriptExtensions")
            )
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

private suspend fun loadUnicodeVersionMetadata(): UnicodeVersionMetadata? {
    cachedUnicodeVersionMetadata?.let { return it }
    return unicodeVersionMetadataCacheMutex.withLock {
        cachedUnicodeVersionMetadata?.let { return it }
        val scriptsText = runCatching {
            Res.readBytes(SCRIPTS_PATH).decodeToString()
        }.getOrNull()
        val scriptExtensionsText = runCatching {
            Res.readBytes(SCRIPT_EXTENSIONS_PATH).decodeToString()
        }.getOrNull()
        val metadata = mergeUnicodeVersionMetadata(
            scriptsText?.let { parseUnicodeVersionMetadata(it, "Scripts") },
            scriptExtensionsText?.let { parseUnicodeVersionMetadata(it, "ScriptExtensions") }
        )
        cachedUnicodeVersionMetadata = metadata
        metadata
    }
}

private suspend fun loadUnicodeDataCodePoints(): IntArray {
    cachedUnicodeDataCodePoints?.let { return it }
    unicodeDataCacheMutex.withLock {
        cachedUnicodeDataCodePoints?.let { return it }
        val bytes = runCatching {
            Res.readBytes(UNICODE_DATA_PATH)
        }.getOrElse {
            throw IllegalStateException(
                "Missing $UNICODE_DATA_PATH. Run ./gradlew :shared:downloadUnicodeCoverageData -Poverwrite or manually download UnicodeData.txt from $UNICODE_DRAFT_UCD_BASE_URL (fallback: $UNICODE_VERSIONED_UCD_BASE_URL) to shared/src/commonMain/composeResources/files/."
            )
        }
        val list = ArrayList<Int>(300_000)
        var pendingRangeStart = -1

        var i = 0
        val n = bytes.size
        while (i < n) {
            val lineStart = i
            // Find line end
            while (i < n && bytes[i] != 0x0A.toByte()) {
                i++
            }
            val lineEnd = i
            i++ // Skip \n

            // Handle empty lines or comments
            if (lineEnd == lineStart || bytes[lineStart] == '#'.code.toByte()) continue

            // Process fields
            var current = lineStart

            // Field 0: Code Point
            val f0Start = current
            while (current < lineEnd && bytes[current] != ';'.code.toByte()) current++
            val f0End = current
            if (current >= lineEnd) continue // Invalid line
            val codePoint = parseHex(bytes, f0Start, f0End)
            current++ // Skip ;

            // Field 1: Name
            val f1Start = current
            while (current < lineEnd && bytes[current] != ';'.code.toByte()) current++
            val f1End = current
            if (current >= lineEnd) continue
            // Check for <... First> or <... Last>
            val isFirst = isRangeStart(bytes, f1Start, f1End)
            val isLast = isRangeEnd(bytes, f1Start, f1End)
            current++ // Skip ;

            // Field 2: Category
            val f2Start = current
            while (current < lineEnd && bytes[current] != ';'.code.toByte()) current++
            val f2End = current
            // Category check
            val isFiltered = isFilteredCategory(bytes, f2Start, f2End)

            if (isFirst) {
                pendingRangeStart = if (isFiltered) -2 else codePoint
                continue
            }

            if (isLast) {
                if (pendingRangeStart >= 0) {
                    for (cp in pendingRangeStart..codePoint) {
                        list.add(cp)
                    }
                }
                pendingRangeStart = -1
                continue
            }

            if (isFiltered) continue
            list.add(codePoint)
        }
        val result = list.distinct().sorted().toIntArray()
        cachedUnicodeDataCodePoints = result
        return result
    }
}

private fun parseHex(bytes: ByteArray, start: Int, end: Int): Int {
    var value = 0
    for (i in start until end) {
        val digit = when (val b = bytes[i].toInt()) {
            in '0'.code..'9'.code -> b - '0'.code
            in 'A'.code..'F'.code -> b - 'A'.code + 10
            in 'a'.code..'f'.code -> b - 'a'.code + 10
            else -> 0
        }
        value = (value shl 4) or digit
    }
    return value
}

private fun isRangeStart(bytes: ByteArray, start: Int, end: Int): Boolean {
    val suffix = "First>"
    if (end - start < suffix.length) return false
    for (i in suffix.indices) {
        if (bytes[end - suffix.length + i] != suffix[i].code.toByte()) return false
    }
    return bytes[start] == '<'.code.toByte()
}

private fun isRangeEnd(bytes: ByteArray, start: Int, end: Int): Boolean {
    val suffix = "Last>"
    if (end - start < suffix.length) return false
    for (i in suffix.indices) {
        if (bytes[end - suffix.length + i] != suffix[i].code.toByte()) return false
    }
    return bytes[start] == '<'.code.toByte()
}

private fun isFilteredCategory(bytes: ByteArray, start: Int, end: Int): Boolean {
    if (end - start != 2) return false
    val b1 = bytes[start].toInt().toChar()
    val b2 = bytes[start + 1].toInt().toChar()
    // "Cc", "Cf", "Co", "Zs", "Zl", "Zp", "Mn", "Cs", "Cn"
    return when (b1) {
        'C' -> b2 == 'c' || b2 == 'f' || b2 == 'o' || b2 == 's' || b2 == 'n'
        'Z' -> b2 == 's' || b2 == 'l' || b2 == 'p'
        'M' -> b2 == 'n'
        else -> false
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

data class UnicodeVersionMetadata(
    val version: String,
    val date: String
)

private data class MutableBlockStatistics(
    var supported: Int = 0,
    var total: Int = 0
)

private fun parseUnicodeVersionMetadata(text: String, filePrefix: String): UnicodeVersionMetadata? {
    var version: String? = null
    var date: String? = null
    val versionRegex = Regex("""^#\s*$filePrefix-([0-9]+(?:\.[0-9]+){2})\.txt$""")
    val dateRegex = Regex("""^#\s*Date:\s*(.+)$""")
    text.lineSequence().forEach { line ->
        if (version == null) {
            val match = versionRegex.find(line.trim())
            if (match != null) {
                version = match.groupValues[1]
            }
        }
        if (date == null) {
            val match = dateRegex.find(line.trim())
            if (match != null) {
                date = match.groupValues[1].trim()
            }
        }
        if (version != null && date != null) {
            return UnicodeVersionMetadata(version = version, date = date)
        }
    }
    return null
}

private fun mergeUnicodeVersionMetadata(
    scripts: UnicodeVersionMetadata?,
    scriptExtensions: UnicodeVersionMetadata?
): UnicodeVersionMetadata? {
    val version = scripts?.version ?: scriptExtensions?.version ?: return null
    val date = scripts?.date ?: scriptExtensions?.date ?: return null
    return UnicodeVersionMetadata(version = version, date = date)
}

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
