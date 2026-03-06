@file:Suppress("UNCHECKED_CAST")

package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import platform.AppKit.NSColor
import platform.AppKit.NSFont
import platform.AppKit.NSFontAttributeName
import platform.AppKit.NSFontDescriptorSystemDesignMonospaced
import platform.AppKit.NSFontDescriptorSystemDesignSerif
import platform.AppKit.NSFontWeightBlack
import platform.AppKit.NSFontWeightBold
import platform.AppKit.NSFontWeightHeavy
import platform.AppKit.NSFontWeightLight
import platform.AppKit.NSFontWeightMedium
import platform.AppKit.NSFontWeightRegular
import platform.AppKit.NSFontWeightSemibold
import platform.AppKit.NSFontWeightThin
import platform.AppKit.NSFontWeightUltraLight
import platform.AppKit.NSForegroundColorAttributeName
import platform.AppKit.NSGraphicsContext
import platform.AppKit.NSMutableParagraphStyle
import platform.AppKit.NSParagraphStyleAttributeName
import platform.AppKit.NSTextAlignmentCenter
import platform.AppKit.NSTextAlignmentRight
import platform.AppKit.boundingRectWithSize
import platform.AppKit.drawInRect
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextGetData
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGContextScaleCTM
import platform.Foundation.NSAttributedString
import platform.Foundation.create
import platform.posix.memcpy
import kotlin.math.abs
import kotlin.math.ceil

private val BITMAP_INFO: UInt = 2u or 8192u // kCGImageAlphaPremultipliedFirst | kCGBitmapByteOrder32Little

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun NativeVariableText(
    text: String,
    fontSize: Float,
    fontWeight: Int,
    color: Color,
    italic: Boolean,
    fontFamily: FontFamily,
    textAlign: TextAlign,
    maxLines: Int,
    modifier: Modifier
) {
    val font = remember(fontSize, fontWeight, italic, fontFamily) {
        val nsWeight = mapComposeWeightToNS(fontWeight)
        val baseFont = NSFont.systemFontOfSize(
            fontSize.toDouble(),
            weight = nsWeight
        )

        val designedFont = when (fontFamily) {
            FontFamily.Serif, FontFamily.Monospace -> {
                val design = if (fontFamily == FontFamily.Serif) NSFontDescriptorSystemDesignSerif else NSFontDescriptorSystemDesignMonospaced
                val designDescriptor = baseFont.fontDescriptor.fontDescriptorWithDesign(design)
                if (designDescriptor != null) {
                    NSFont.fontWithDescriptor(designDescriptor, fontSize.toDouble()) ?: baseFont
                } else {
                    baseFont
                }
            }

            else -> baseFont
        }

        if (italic) {
            val italicDescriptor = designedFont.fontDescriptor.fontDescriptorWithSymbolicTraits(
                1u
            )
            NSFont.fontWithDescriptor(italicDescriptor, fontSize.toDouble()) ?: designedFont
        } else {
            designedFont
        }
    }

    val scale = 2.0

    val attrString = remember(text, font, color, textAlign) {
        val nsColor = NSColor.colorWithSRGBRed(
            red = color.red.toDouble(),
            green = color.green.toDouble(),
            blue = color.blue.toDouble(),
            alpha = color.alpha.toDouble()
        )
        val paragraphStyle = NSMutableParagraphStyle().apply {
            setAlignment(
                when (textAlign) {
                    TextAlign.Center -> NSTextAlignmentCenter
                    TextAlign.End, TextAlign.Right -> NSTextAlignmentRight
                    else -> 0L
                }
            )
        }
        NSAttributedString.create(
            string = text,
            attributes = mapOf(
                NSFontAttributeName to font,
                NSForegroundColorAttributeName to nsColor,
                NSParagraphStyleAttributeName to paragraphStyle
            ) as Map<Any?, *>
        )
    }

    val textSize: Pair<Double, Double> = remember(attrString, maxLines) {
        val maxWidth = if (maxLines == 1) 100000.0 else 10000.0
        val maxHeight = if (maxLines == Int.MAX_VALUE) 100000.0
        else font.ascender + abs(font.descender) + (font.ascender + abs(font.descender) + font.leading) * (maxLines - 1)
        attrString.boundingRectWithSize(
            size = platform.CoreGraphics.CGSizeMake(maxWidth, maxHeight),
            options = 1L,
            context = null
        ).useContents {
            ceil(size.width) to ceil(size.height)
        }
    }

    val imageBitmap = remember(attrString, scale, textSize) {
        val width = textSize.first
        val height = textSize.second
        if (width <= 0.0 || height <= 0.0) return@remember null

        val pixelWidth = (width * scale).toInt()
        val pixelHeight = (height * scale).toInt()
        if (pixelWidth <= 0 || pixelHeight <= 0) return@remember null

        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val context = CGBitmapContextCreate(
            data = null,
            width = pixelWidth.toULong(),
            height = pixelHeight.toULong(),
            bitsPerComponent = 8u,
            bytesPerRow = (pixelWidth * 4).toULong(),
            space = colorSpace,
            bitmapInfo = BITMAP_INFO
        )
        CGColorSpaceRelease(colorSpace)
        if (context == null) return@remember null

        CGContextScaleCTM(context, scale, scale)

        val nsContext = NSGraphicsContext.graphicsContextWithCGContext(context, flipped = true)
        NSGraphicsContext.saveGraphicsState()
        NSGraphicsContext.setCurrentContext(nsContext)

        attrString.drawInRect(platform.CoreGraphics.CGRectMake(0.0, 0.0, width, height))

        NSGraphicsContext.restoreGraphicsState()

        val dataPtr = CGBitmapContextGetData(context)
        if (dataPtr == null) {
            CGContextRelease(context)
            return@remember null
        }

        val byteCount = pixelWidth * pixelHeight * 4
        val rawBytes = ByteArray(byteCount)
        rawBytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), dataPtr, byteCount.toULong())
        }
        CGContextRelease(context)

        val rowBytes = pixelWidth * 4
        val bytes = ByteArray(byteCount)
        for (row in 0 until pixelHeight) {
            rawBytes.copyInto(bytes, (pixelHeight - 1 - row) * rowBytes, row * rowBytes, (row + 1) * rowBytes)
        }

        val bitmap = Bitmap()
        bitmap.allocPixels(ImageInfo(pixelWidth, pixelHeight, ColorType.BGRA_8888, ColorAlphaType.PREMUL))
        bitmap.installPixels(bytes)
        bitmap.asComposeImageBitmap()
    }

    val sizeModifier = Modifier.width(textSize.first.dp).height(textSize.second.dp)

    if (imageBitmap != null) {
        Canvas(modifier = modifier.then(sizeModifier)) {
            val offsetX = when (textAlign) {
                TextAlign.Center -> ((size.width - imageBitmap.width) / 2f).toInt()
                TextAlign.End, TextAlign.Right -> (size.width - imageBitmap.width).toInt()
                else -> 0
            }
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(offsetX, 0),
                srcSize = IntSize(imageBitmap.width, imageBitmap.height),
                dstSize = IntSize(imageBitmap.width, imageBitmap.height)
            )
        }
    }
}

private data class WeightAnchor(val composeWeight: Int, val nsWeight: Double)

private val weightAnchors = listOf(
    WeightAnchor(100, NSFontWeightUltraLight),
    WeightAnchor(200, NSFontWeightThin),
    WeightAnchor(300, NSFontWeightLight),
    WeightAnchor(400, NSFontWeightRegular),
    WeightAnchor(500, NSFontWeightMedium),
    WeightAnchor(600, NSFontWeightSemibold),
    WeightAnchor(700, NSFontWeightBold),
    WeightAnchor(800, NSFontWeightHeavy),
    WeightAnchor(900, NSFontWeightBlack)
)

private fun mapComposeWeightToNS(composeWeight: Int): Double {
    val clamped = composeWeight.coerceIn(1, 1000)

    if (clamped <= weightAnchors.first().composeWeight) return weightAnchors.first().nsWeight
    if (clamped >= weightAnchors.last().composeWeight) return weightAnchors.last().nsWeight

    for (i in 0 until weightAnchors.size - 1) {
        val lo = weightAnchors[i]
        val hi = weightAnchors[i + 1]
        if (clamped in lo.composeWeight..hi.composeWeight) {
            val t = (clamped - lo.composeWeight).toDouble() / (hi.composeWeight - lo.composeWeight)
            return lo.nsWeight + t * (hi.nsWeight - lo.nsWeight)
        }
    }

    return NSFontWeightRegular
}
