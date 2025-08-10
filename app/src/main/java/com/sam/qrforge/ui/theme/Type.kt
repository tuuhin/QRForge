package com.sam.qrforge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.sam.qrforge.R

private val provider = GoogleFont.Provider(
	providerAuthority = "com.google.android.gms.fonts",
	providerPackage = "com.google.android.gms",
	certificates = R.array.com_google_android_gms_fonts_certs
)

val MONTSERRAT_FONT_FAMILY = FontFamily(
	Font(
		googleFont = GoogleFont("Montserrat"),
		fontProvider = provider,
	)
)

// Default Material 3 typography values
private val baseline = Typography()

val AppTypography = Typography(
	displayLarge = baseline.displayLarge.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	displayMedium = baseline.displayMedium.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	displaySmall = baseline.displaySmall.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	headlineLarge = baseline.headlineLarge.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	headlineMedium = baseline.headlineMedium.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	headlineSmall = baseline.headlineSmall.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	titleLarge = baseline.titleLarge.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	titleMedium = baseline.titleMedium.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
	titleSmall = baseline.titleSmall.copy(fontFamily = MONTSERRAT_FONT_FAMILY),
)