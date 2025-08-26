package com.sam.qrforge.data.repository

import android.database.SQLException
import com.sam.qrforge.data.database.dao.QRDataDao
import com.sam.qrforge.data.mappers.toEntity
import com.sam.qrforge.data.mappers.toModel
import com.sam.qrforge.domain.models.CreateNewQRModel
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.domain.repository.exception.CannotFindMatchingIdException
import com.sam.qrforge.domain.repository.exception.UnableToProcessSQLException
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SaveQRDataRepoImpl(private val dao: QRDataDao) : SavedQRDataRepository {

	override suspend fun insertQRData(model: CreateNewQRModel): Result<SavedQRModel> {
		return try {
			val id = dao.insertNewQR(model.toEntity())
			val new = dao.fetchQREntityById(id)
				?: return Result.failure(CannotFindMatchingIdException())
			Result.success(new.toModel())
		} catch (_: SQLException) {
			Result.failure(UnableToProcessSQLException())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override suspend fun updateQRModel(model: SavedQRModel): Result<SavedQRModel> {
		return try {
			dao.insertNewQR(model.toEntity())
			val new = dao.fetchQREntityById(model.id)
				?: return Result.failure(CannotFindMatchingIdException())
			Result.success(new.toModel())
		} catch (_: SQLException) {
			Result.failure(UnableToProcessSQLException())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override fun fetchAllSavedQR(): Flow<Resource<List<SavedQRModel>, Exception>> {
		return dao.fetchAllQREntity()
			.map { entities ->
				val models = entities.map { it.toModel() }
				Resource.Success<List<SavedQRModel>, Exception>(models)
						as Resource<List<SavedQRModel>, Exception>
			}
			.flowOn(Dispatchers.IO)
			.onStart { emit(Resource.Loading) }
			.catch { err ->
				if (err is SQLException) emit(
					Resource.Error(
						UnableToProcessSQLException(),
						"Issue with db cannot read data"
					)
				)
				else emit(Resource.Error(Exception(err)))
			}
	}

	override suspend fun deleteQRModel(model: SavedQRModel): Result<Unit> {
		return try {
			dao.deleteQREntity(model.toEntity())
			Result.success(Unit)
		} catch (_: SQLException) {
			Result.failure(UnableToProcessSQLException())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}