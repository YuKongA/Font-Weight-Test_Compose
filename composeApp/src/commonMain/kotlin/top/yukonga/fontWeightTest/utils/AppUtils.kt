package top.yukonga.fontWeightTest.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.font.FontWeight
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.misans_black
import fontweighttest.composeapp.generated.resources.misans_bold
import fontweighttest.composeapp.generated.resources.misans_extrabold
import fontweighttest.composeapp.generated.resources.misans_extralight
import fontweighttest.composeapp.generated.resources.misans_light
import fontweighttest.composeapp.generated.resources.misans_medium
import fontweighttest.composeapp.generated.resources.misans_normal
import fontweighttest.composeapp.generated.resources.misans_semibold
import fontweighttest.composeapp.generated.resources.misans_thin

@Stable
val fontWeightList = listOf(
    FontWeight.Thin, // W100
    FontWeight.ExtraLight, // W200
    FontWeight.Light, // W300
    FontWeight.Normal, // W400
    FontWeight.Medium, // W500
    FontWeight.SemiBold, // W600
    FontWeight.Bold, // W700
    FontWeight.ExtraBold, // W800
    FontWeight.Black // W900
)

@Stable
val miSansList = listOf(
    Res.font.misans_thin, // W100
    Res.font.misans_extralight, // W200
    Res.font.misans_light, // W300
    Res.font.misans_normal, // W400
    Res.font.misans_medium, // W500
    Res.font.misans_semibold, // W600
    Res.font.misans_bold, // W700
    Res.font.misans_extrabold, // W800
    Res.font.misans_black // W900
)

@Stable
val fontWeightDescriptions = listOf(
    "淡体 Thin (Hairline)", // W100
    "特细 ExtraLight (UltraLight)", // W200
    "细体 Light", // W300
    "标准 Normal (Regular)", // W400
    "适中 Medium", // W500
    "次粗 SemiBold (DemiBold)", // W600
    "粗体 Bold", // W700
    "特粗 ExtraBold (UltraBold)", // W800
    "浓体 Black (Heavy)" // W900
)

@Stable
val testCharacters = listOf("永", "の", "A", "6")
