package com.sam.qrforge.di

import android.content.Context
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
import com.sam.qrforge.domain.util.PlatformCoroutineDispatchers
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
class MLKitModule {

    @Single
    fun providesBarcodeScanning(): BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        return BarcodeScanning.getClient(options)
    }

    @Single
    fun providesMLKitQRScanner(
        context: Context,
        scanner: BarcodeScanner,
        dispatchers: PlatformCoroutineDispatchers,
    ): QRScannerFacade = MLKitQRScannerFacadeImpl(context, scanner, dispatchers)

    @Factory
    fun providesQRImageAnalyzer(
        facade: QRScannerFacade,
        dispatchers: PlatformCoroutineDispatchers,
    ): QRImageAnalyzer = QRImageAnalyzerImpl(facade, dispatchers)

    @Factory
    @Named("ML_KIT")
    fun providesValidatorFacade(
        scanner: BarcodeScanner,
        dispatchers: PlatformCoroutineDispatchers,
    ): QRValidatorFacade = MLKitQRValidatorFacadeImpl(scanner, dispatchers)
}
