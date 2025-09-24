package com.sam.qrforge.di

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.sam.qrforge.data.facade.MLKitQRScannerFacadeImpl
import com.sam.qrforge.data.facade.MLKitQRValidatorFacadeImpl
import com.sam.qrforge.data.facade.QRImageAnalyzerImpl
import com.sam.qrforge.domain.facade.QRImageAnalyzer
import com.sam.qrforge.domain.facade.QRScannerFacade
import com.sam.qrforge.domain.facade.QRValidatorFacade
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mlKitModule = module {
	single {
		val options = BarcodeScannerOptions.Builder()
			.setBarcodeFormats(Barcode.FORMAT_QR_CODE)
			.build()

		BarcodeScanning.getClient(options)
	}.bind<BarcodeScanner>()

	singleOf(::MLKitQRScannerFacadeImpl).bind<QRScannerFacade>()

	// image analyzer
	factoryOf(::QRImageAnalyzerImpl).bind<QRImageAnalyzer>()
	// validator
	factoryOf(
		::MLKitQRValidatorFacadeImpl,
		options = { named("ML_KIT") },
	).bind<QRValidatorFacade>()
}