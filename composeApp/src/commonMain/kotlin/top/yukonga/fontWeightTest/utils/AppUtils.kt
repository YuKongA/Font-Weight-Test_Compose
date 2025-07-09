package top.yukonga.fontWeightTest.utils

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
