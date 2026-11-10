package com.sam.qrforge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp

val AppTypographyCustom: Typography
    get() {
        val instrumentSerifSemiBold =
            instrumentSerif(FontVariation.Settings(FontVariation.weight(600)))
        val instrumentSerifBold =
            instrumentSerif(FontVariation.Settings(FontVariation.weight(700)))
        val instrumentSerifExtraBold =
            instrumentSerif(FontVariation.Settings(FontVariation.weight(800)))

        // --- Bricolage Grotesque: Titles ---
        val titleMediumFont =
            bricolageGrotesque(FontVariation.Settings(FontVariation.weight(500)))
        val titleSemiBoldFont =
            bricolageGrotesque(FontVariation.Settings(FontVariation.weight(600)))
        val titleBoldFont =
            bricolageGrotesque(FontVariation.Settings(FontVariation.weight(700)))

        // --- Google Sans Flex: Body and Labels ---
        val uiFont =
            sansFlex(FontVariation.Settings(FontVariation.weight(400)))
        val uiMediumFont =
            sansFlex(FontVariation.Settings(FontVariation.weight(500)))
        val uiStrongFont =
            geistMono(FontVariation.Settings(FontVariation.weight(600)))
        val uiBoldFont =
            sansFlex(FontVariation.Settings(FontVariation.weight(700)))

        return Typography(
            displayLarge = TextStyle(
                fontFamily = instrumentSerifBold,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.5).sp,
            ),
            displayMedium = TextStyle(
                fontFamily = instrumentSerifBold,
                fontSize = 45.sp,
                lineHeight = 52.sp,
                letterSpacing = (-0.25).sp,
            ),
            displaySmall = TextStyle(
                fontFamily = instrumentSerifBold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.sp,
            ),

            headlineLarge = TextStyle(
                fontFamily = instrumentSerifSemiBold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.15).sp,
            ),
            headlineMedium = TextStyle(
                fontFamily = instrumentSerifSemiBold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = 0.sp,
            ),
            headlineSmall = TextStyle(
                fontFamily = instrumentSerifSemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),

            titleLarge = TextStyle(
                fontFamily = titleBoldFont,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
            titleMedium = TextStyle(
                fontFamily = titleMediumFont,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
            titleSmall = TextStyle(
                fontFamily = titleMediumFont,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),

            bodyLarge = TextStyle(
                fontFamily = uiFont,
                fontSize = 17.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.sp,
            ),
            bodyMedium = TextStyle(
                fontFamily = uiFont,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),
            bodySmall = TextStyle(
                fontFamily = uiFont,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.sp,
            ),

            labelLarge = TextStyle(
                fontFamily = uiStrongFont,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
            labelMedium = TextStyle(
                fontFamily = uiMediumFont,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.3.sp,
            ),
            labelSmall = TextStyle(
                fontFamily = uiMediumFont,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),

            // --- Material 3 Expressive Emphasized Styles ---
            // Display and headline emphasis: Fraunces
            displayLargeEmphasized = TextStyle(
                fontFamily = instrumentSerifExtraBold,
                fontSize = 64.sp,
                lineHeight = 72.sp,
                letterSpacing = (-0.75).sp,
            ),
            displayMediumEmphasized = TextStyle(
                fontFamily = instrumentSerifExtraBold,
                fontSize = 50.sp,
                lineHeight = 58.sp,
                letterSpacing = (-0.5).sp,
            ),
            headlineLargeEmphasized = TextStyle(
                fontFamily = instrumentSerifBold,
                fontSize = 34.sp,
                lineHeight = 42.sp,
                letterSpacing = (-0.15).sp,
            ),

            // Title emphasis: Bricolage Grotesque
            titleLargeEmphasized = TextStyle(
                fontFamily = titleBoldFont,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
            titleMediumEmphasized = TextStyle(
                fontFamily = titleSemiBoldFont,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
            titleSmallEmphasized = TextStyle(
                fontFamily = titleSemiBoldFont,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),

            // Body emphasis: Google Sans Flex
            bodyLargeEmphasized = TextStyle(
                fontFamily = uiStrongFont,
                fontSize = 17.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.sp,
            ),
            bodyMediumEmphasized = TextStyle(
                fontFamily = uiStrongFont,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
            ),
            bodySmallEmphasized = TextStyle(
                fontFamily = uiStrongFont,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.sp,
            ),

            // Label emphasis: Google Sans Flex
            labelLargeEmphasized = TextStyle(
                fontFamily = uiBoldFont,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
            labelMediumEmphasized = TextStyle(
                fontFamily = uiStrongFont,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.3.sp,
            ),
            labelSmallEmphasized = TextStyle(
                fontFamily = uiStrongFont,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),
        )
    }
