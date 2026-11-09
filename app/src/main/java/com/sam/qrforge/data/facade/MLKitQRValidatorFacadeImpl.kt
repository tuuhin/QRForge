package com.sam.qrforge.data.facade

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.createBitmap
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import com.sam.qrforge.data.utils.await
import com.sam.qrforge.domain.facade.QRValidatorFacade
import com.sam.qrforge.domain.models.GeneratedARGBQRModel
import com.sam.qrforge.domain.util.PlatformCoroutineDispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class MLKitQRValidatorFacadeImpl(
    private val scanner: BarcodeScanner,
    private val dispatchers: PlatformCoroutineDispatchers,
) : QRValidatorFacade {

    override suspend fun isValid(rgbaModel: GeneratedARGBQRModel): Result<Boolean> {
        return withContext(dispatchers.io) {
            val bitmap = createBitmap(
                width = rgbaModel.width,
                height = rgbaModel.height,
                config = Bitmap.Config.ARGB_8888,
            )
            try {
                bitmap.setPixels(
                    rgbaModel.pixels,
                    0,
                    rgbaModel.width,
                    0,
                    0,
                    rgbaModel.width,
                    rgbaModel.height,
                )
                val image = InputImage.fromBitmap(bitmap, 0)
                val barcodes = scanner.process(image).await()
                val isQRPresent = barcodes.isNotEmpty()
                Log.d(TAG, "QR FOUND IN THIS IMAGE $isQRPresent")
                Result.success(isQRPresent)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
                Result.failure(e)
            } finally {
                Log.d(TAG, "BITMAP RECYCLED!")
                bitmap.recycle()
            }
        }
    }

    companion object {
        private const val TAG = "ML_KIT_QR_VALIDATOR"
    }
}
