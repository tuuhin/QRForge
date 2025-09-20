package com.sam.qrforge.domain.facade

import com.sam.qrforge.domain.models.GeneratedARGBQRModel

fun interface QRValidatorFacade {

	suspend fun isValid(rgbaModel: GeneratedARGBQRModel): Result<Boolean>
}