package com.sam.qrforge.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sam.qrforge.data.database.converters.LocalDateTimeConvertor
import com.sam.qrforge.data.database.converters.QRDataTypeConvertor
import com.sam.qrforge.data.database.dao.QRDataDao
import com.sam.qrforge.data.database.entity.QRDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@Database(
	entities = [QRDataEntity::class],
	exportSchema = true,
	version = 1
)
@TypeConverters(
	value = [
		QRDataTypeConvertor::class,
		LocalDateTimeConvertor::class,
	]
)
abstract class QRForgeAppDatabase : RoomDatabase() {

	abstract fun qrDao(): QRDataDao

	companion object {
		private var _database: QRForgeAppDatabase? = null
		private const val DB_NAME = "APP_DATABASE"

		fun createDatabase(context: Context): QRForgeAppDatabase {
			return synchronized(this) {
				if (_database == null) {
					_database = Room.databaseBuilder(
						context,
						klass = QRForgeAppDatabase::class.java,
						name = DB_NAME
					)
						.addTypeConverter(QRDataTypeConvertor())
						.addTypeConverter(LocalDateTimeConvertor())
						.setQueryExecutor(Dispatchers.IO.asExecutor())
						.build()
				}
				_database!!
			}
		}
	}
}