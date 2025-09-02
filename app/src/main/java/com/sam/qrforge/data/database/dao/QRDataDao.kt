package com.sam.qrforge.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.sam.qrforge.data.database.entity.QRDataEntity
import com.sam.qrforge.domain.enums.QRDataType
import kotlinx.coroutines.flow.Flow

@Dao
interface QRDataDao {

	@Upsert
	suspend fun insertNewQR(entity: QRDataEntity): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertNewQRBulk(entity: List<QRDataEntity>)

	@Query("SELECT * FROM SAVED_QR_ENTITY")
	fun fetchAllQREntity(): Flow<List<QRDataEntity>>

	@Query("SELECT * FROM SAVED_QR_ENTITY WHERE _ID=:id")
	suspend fun fetchQREntityById(id: Long): QRDataEntity?

	@Query("SELECT * FROM SAVED_QR_ENTITY WHERE _ID=:id")
	fun fetchQREntityByIdFlow(id: Long): Flow<QRDataEntity?>

	@Query("SELECT * FROM SAVED_QR_ENTITY WHERE CONTENT_TYPE=:type")
	fun fetchAllQREntityTyped(type: QRDataType): Flow<List<QRDataEntity>>

	@Query("UPDATE SAVED_QR_ENTITY SET IS_FAV=:isFavourite WHERE _ID=:id")
	suspend fun updateIsFavWithMatchingId(isFavourite: Boolean, id: Long)

	@Delete
	suspend fun deleteQREntity(entity: QRDataEntity)

	@Delete
	suspend fun deleteQREntityBulk(entity: List<QRDataEntity>)
}