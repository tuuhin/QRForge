package com.sam.qrforge.domain.repository

import com.sam.qrforge.domain.models.CreateNewQRModel
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SavedQRDataRepository {

	suspend fun insertQRData(model: CreateNewQRModel): Result<SavedQRModel>

	fun fetchAllSavedQR(): Flow<Resource<List<SavedQRModel>, Exception>>

	suspend fun deleteQRModel(model: SavedQRModel): Result<Unit>

	suspend fun updateQRModel(model: SavedQRModel): Result<SavedQRModel>

	fun fetchQRById(id: Long): Flow<Resource<SavedQRModel, Exception>>

}