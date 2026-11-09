package com.sam.qrforge.data.database

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.sam.qrforge.data.database.converters.LocalDateTimeConvertor
import com.sam.qrforge.data.database.converters.QRDataTypeConvertor
import com.sam.qrforge.data.database.dao.QRDataDao
import com.sam.qrforge.data.database.entity.QRDataEntity
import kotlinx.coroutines.Dispatchers

@Database(
	entities = [QRDataEntity::class],
	exportSchema = true,
	version = 1
)
@ColumnTypeConverters(
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
						.addColumnTypeConverter(QRDataTypeConvertor())
						.addColumnTypeConverter(LocalDateTimeConvertor())
						.setQueryCoroutineContext(Dispatchers.IO)
						.build()
				}
				_database!!
			}
		}

		fun createInMemoryDatabase(context: Context): QRForgeAppDatabase {
			return Room.inMemoryDatabaseBuilder(
				context = context,
				klass = QRForgeAppDatabase::class.java,
			)
				.addColumnTypeConverter(QRDataTypeConvertor())
				.addColumnTypeConverter(LocalDateTimeConvertor())
				.setQueryCoroutineContext(Dispatchers.IO)
				.build()
		}
	}
}