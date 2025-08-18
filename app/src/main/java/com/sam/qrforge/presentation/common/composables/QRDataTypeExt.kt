package com.sam.qrforge.presentation.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType

val QRDataType.painter: Painter
	@Composable
	get() = when (this) {
		QRDataType.TYPE_TEXT -> painterResource(R.drawable.ic_text)
		QRDataType.TYPE_EMAIL -> painterResource(R.drawable.ic_email)
		QRDataType.TYPE_PHONE -> painterResource(R.drawable.ic_phone)
		QRDataType.TYPE_GEO -> painterResource(R.drawable.ic_geo)
		QRDataType.TYPE_URL -> painterResource(R.drawable.ic_url)
		QRDataType.TYPE_WIFI -> painterResource(R.drawable.ic_wifi)
		QRDataType.TYPE_SMS -> painterResource(R.drawable.ic_sms)
	}

val QRDataType.string: String
	@Composable
	get() = when (this) {
		QRDataType.TYPE_EMAIL -> stringResource(R.string.qr_format_email)
		QRDataType.TYPE_PHONE -> stringResource(R.string.qr_format_phone)
		QRDataType.TYPE_GEO -> stringResource(R.string.qr_format_geo)
		QRDataType.TYPE_URL -> stringResource(R.string.qr_format_url)
		QRDataType.TYPE_WIFI -> stringResource(R.string.qr_format_wifi)
		QRDataType.TYPE_TEXT -> stringResource(R.string.qr_format_text)
		QRDataType.TYPE_SMS -> stringResource(R.string.qr_format_sms)
	}
