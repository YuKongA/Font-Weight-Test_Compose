@file:Suppress("UNCHECKED_CAST")

package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.platform.LocalDensity
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
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGBitmapContextGetData
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGContextRelease
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSAttributedString
import platform.Foundation.create
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.NSMutableParagraphStyle
import platform.UIKit.NSParagraphStyleAttributeName
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.NSTextAlignmentRight
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIFontDescriptorSystemDesignMonospaced
import platform.UIKit.UIFontDescriptorSystemDesignSerif
import platform.UIKit.UIFontWeightBlack
import platform.UIKit.UIFontWeightBold
import platform.UIKit.UIFontWeightHeavy
import platform.UIKit.UIFontWeightLight
import platform.UIKit.UIFontWeightMedium
import platform.UIKit.UIFontWeightRegular
import platform.UIKit.UIFontWeightSemibold
import platform.UIKit.UIFontWeightThin
import platform.UIKit.UIFontWeightUltraLight
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIScreen
import platform.UIKit.boundingRectWithSize
import platform.UIKit.drawInRect
import platform.posix.memcpy
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
        val uiKitWeight = mapComposeWeightToUIKit(fontWeight)
        val baseFont = UIFont.systemFontOfSize(
            fontSize.toDouble(),
            weight = uiKitWeight
        )

        val designedFont = when (fontFamily) {
            FontFamily.Serif, FontFamily.Monospace -> {
                val design = if (fontFamily == FontFamily.Serif) UIFontDescriptorSystemDesignSerif else UIFontDescriptorSystemDesignMonospaced
                val designDescriptor = baseFont.fontDescriptor.fontDescriptorWithDesign(design)
                if (designDescriptor != null) {
                    UIFont.fontWithDescriptor(designDescriptor, fontSize.toDouble())
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
            if (italicDescriptor != null) {
                UIFont.fontWithDescriptor(italicDescriptor, fontSize.toDouble())
            } else {
                designedFont
            }
        } else {
            designedFont
        }
    }

    val scale = UIScreen.mainScreen.scale
    val density = LocalDensity.current.density

    val attrString = remember(text, font, color, textAlign) {
        val uiColor = UIColor(
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
                NSForegroundColorAttributeName to uiColor,
                NSParagraphStyleAttributeName to paragraphStyle
            ) as Map<Any?, *>
        )
    }

    BoxWithConstraints(modifier = modifier) {
        val containerWidthPt = constraints.maxWidth / density.toDouble()

        val textSize: Pair<Double, Double> = remember(attrString, maxLines, containerWidthPt) {
            val measureWidth = if (maxLines == 1) 100000.0 else containerWidthPt
            val maxHeight = if (maxLines == Int.MAX_VALUE) 100000.0
            else font.lineHeight * maxLines + font.leading * (maxLines - 1)
            attrString.boundingRectWithSize(
                size = CGSizeMake(measureWidth, maxHeight),
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

            UIGraphicsBeginImageContextWithOptions(CGSizeMake(width, height), false, scale)
            attrString.drawInRect(CGRectMake(0.0, 0.0, width, height))
            val uiImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            val cgImage = uiImage?.CGImage ?: return@remember null
            val pixelWidth = CGImageGetWidth(cgImage).toInt()
            val pixelHeight = CGImageGetHeight(cgImage).toInt()
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

            CGContextDrawImage(context, CGRectMake(0.0, 0.0, pixelWidth.toDouble(), pixelHeight.toDouble()), cgImage)

            val dataPtr = CGBitmapContextGetData(context)
            if (dataPtr == null) {
                CGContextRelease(context)
                return@remember null
            }

            val byteCount = pixelWidth * pixelHeight * 4
            val bytes = ByteArray(byteCount)
            bytes.usePinned { pinned ->
                memcpy(pinned.addressOf(0), dataPtr, byteCount.toULong())
            }
            CGContextRelease(context)

            val bitmap = Bitmap()
            bitmap.allocPixels(ImageInfo(pixelWidth, pixelHeight, ColorType.BGRA_8888, ColorAlphaType.PREMUL))
            bitmap.installPixels(bytes)
            bitmap.asComposeImageBitmap()
        }

        if (imageBitmap != null) {
            Canvas(modifier = Modifier.width(textSize.first.dp).height(textSize.second.dp)) {
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
}

private data class WeightAnchor(val composeWeight: Int, val uiKitWeight: Double)

private val weightAnchors = listOf(
    WeightAnchor(100, UIFontWeightUltraLight),
    WeightAnchor(200, UIFontWeightThin),
    WeightAnchor(300, UIFontWeightLight),
    WeightAnchor(400, UIFontWeightRegular),
    WeightAnchor(500, UIFontWeightMedium),
    WeightAnchor(600, UIFontWeightSemibold),
    WeightAnchor(700, UIFontWeightBold),
    WeightAnchor(800, UIFontWeightHeavy),
    WeightAnchor(900, UIFontWeightBlack)
)

private fun mapComposeWeightToUIKit(composeWeight: Int): Double {
    val clamped = composeWeight.coerceIn(1, 1000)

    if (clamped <= weightAnchors.first().composeWeight) return weightAnchors.first().uiKitWeight
    if (clamped >= weightAnchors.last().composeWeight) return weightAnchors.last().uiKitWeight

    for (i in 0 until weightAnchors.size - 1) {
        val lo = weightAnchors[i]
        val hi = weightAnchors[i + 1]
        if (clamped in lo.composeWeight..hi.composeWeight) {
            val t = (clamped - lo.composeWeight).toDouble() / (hi.composeWeight - lo.composeWeight)
            return lo.uiKitWeight + t * (hi.uiKitWeight - lo.uiKitWeight)
        }
    }

    return UIFontWeightRegular
}
