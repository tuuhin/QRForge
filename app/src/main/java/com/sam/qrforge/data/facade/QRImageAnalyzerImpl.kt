package com.sam.qrforge.data.facade

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.sam.qrforge.data.utils.toNV21
import com.sam.qrforge.domain.facade.QRImageAnalyzer
import com.sam.qrforge.domain.facade.QRScannerFacade
import com.sam.qrforge.domain.facade.exception.NoQRCodeFoundException
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.util.PlatformCoroutineDispatchers
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalAtomicApi::class)
class QRImageAnalyzerImpl(
    private val scanner: QRScannerFacade,
    dispatchers: PlatformCoroutineDispatchers,
) : QRImageAnalyzer {

    private val _scope = CoroutineScope(dispatchers.io + SupervisorJob())
    private val _isInProgress = AtomicBoolean(false)

    // only keep the latest results in the flow
    private val _results = MutableSharedFlow<Result<QRContentModel>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val resultAnalysis: Flow<Result<QRContentModel>> = _results.asSharedFlow()

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        // if the computation is in progress don't allow the successive frame
        if (!_isInProgress.compareAndSet(expectedValue = false, newValue = true)) {
            image.close()
            return
        }

        // extract all the values here
        val width = image.width
        val height = image.height
        val rotation = image.imageInfo.rotationDegrees

        val imageObject = image.image
        if (imageObject == null) {
            Log.w(TAG, "NO IMAGE FOUND CANCELLING OPERATION")
            image.close()
            _isInProgress.store(false)
            return
        }

        _scope.launch {
            try {
                val nv21Bytes = try {
                    imageObject.toNV21()
                } catch (_: Exception) {
                    Log.w(TAG, "FAILED TO READ IN NV21 array")
                    return@launch
                } finally {
                    image.close()
                }

                val result = scanner.decodeAsNV21Source(
                    nv21Bytes,
                    width,
                    height,
                    rotation,
                )
                result.fold(
                    onSuccess = {
                        _results.tryEmit(Result.success(it))
                    },
                    onFailure = { err ->
                        if (err is NoQRCodeFoundException) return@fold
                        _results.tryEmit(Result.failure(err))
                    },
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _results.tryEmit(Result.failure(e))
            }
        }.invokeOnCompletion {
            _isInProgress.store(false)
        }
    }

    override fun cleanUp() {
        Log.i(TAG, "CANCELLING AL JOBS")
        _scope.coroutineContext.cancelChildren()
        _isInProgress.store(false)
    }

    companion object {
        private const val TAG = "QRImageAnalyzer"
    }
}
