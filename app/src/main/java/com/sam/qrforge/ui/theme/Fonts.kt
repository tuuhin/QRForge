package com.sam.qrforge.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.sam.qrforge.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

fun instrumentSerif(settings: FontVariation.Settings) = FontFamily(
    Font(
        googleFont = GoogleFont("Instrument Serif"),
        fontProvider = provider,
        variationSettings = settings,
    ),
)

fun bricolageGrotesque(settings: FontVariation.Settings): FontFamily {
    return FontFamily(
        Font(
            GoogleFont("Bricolage Grotesque"),
            fontProvider = provider,
            variationSettings = settings,
        ),
    )
}

fun sansFlex(settings: FontVariation.Settings) = FontFamily(
    Font(
        googleFont = GoogleFont("Google Sans Flex"),
        fontProvider = provider,
        variationSettings = settings,
    ),
)

fun geistMono(settings: FontVariation.Settings): FontFamily {
    return FontFamily(
        Font(
            GoogleFont("Geist Mono"),
            fontProvider = provider,
            variationSettings = settings,
        ),
    )
}
