package com.sam.qrforge.data.database

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DBMigrationTest {

	private val instrumentation by lazy { InstrumentationRegistry.getInstrumentation() }
	private val dbFileName by lazy { instrumentation.targetContext.getDatabasePath("qrforge-test-db.db") }

	@get:Rule
	val migrationTestHelper = MigrationTestHelper(
		instrumentation = instrumentation,
		databaseClass = QRForgeAppDatabase::class,
		driver = AndroidSQLiteDriver(),
		file = dbFileName,
	)

	@Test
	fun given_exported_version_one_schema_when_created_then_saved_qr_table_is_available() =
		runTest {
			// delete the file
			dbFileName.delete()
			// perform migrations
			val connection = migrationTestHelper.createDatabase(version = 1)
			advanceUntilIdle()

			connection.prepare("SELECT COUNT(*) FROM SAVED_QR_ENTITY").use { statement ->
				assertThat(statement.step()).isTrue()
				assertThat(statement.getLong(0)).isEqualTo(0L)
			}
		}
}