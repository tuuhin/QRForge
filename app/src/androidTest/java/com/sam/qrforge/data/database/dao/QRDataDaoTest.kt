package com.sam.qrforge.data.database.dao

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.sam.qrforge.data.database.QRForgeAppDatabase
import com.sam.qrforge.data.database.entity.QRDataEntity
import com.sam.qrforge.domain.enums.QRDataType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class QRDataDaoTest {

	private lateinit var database: QRForgeAppDatabase
	private lateinit var dao: QRDataDao

	@BeforeTest
	fun setUp() {
		database =
			QRForgeAppDatabase.createInMemoryDatabase(ApplicationProvider.getApplicationContext())
		dao = database.qrDao()
	}

	@AfterTest
	fun tearDown() {
		database.close()
	}

	@Test
	fun given_new_entity_when_insert_then_entity_round_trips_with_converters() = runTest {
		val entity = createNewEntry(id = 1L)

		dao.insertNewQR(entity)
		advanceUntilIdle()
		assertThat(dao.fetchQREntityById(1L)).isEqualTo(entity)
	}

	@Test
	fun given_existing_id_when_insert_then_existing_entity_is_replaced() = runTest {
		dao.insertNewQR(createNewEntry(id = 1L, title = "Original"))

		val replacement = createNewEntry(id = 1L, title = "Updated", type = QRDataType.TYPE_URL)

		dao.insertNewQR(replacement)
		advanceUntilIdle()

		assertThat(dao.fetchQREntityById(1)).isEqualTo(replacement)
	}

	@Test
	fun given_entities_when_bulk_insert_then_all_rows_are_persisted() = runTest {

		val entities = listOf(createNewEntry(id = 1L), createNewEntry(id = 2L, type = QRDataType.TYPE_SMS))
		dao.insertNewQRBulk(entities)

		val savedData = dao.fetchAllQREntities().sortedBy { it.id }

		advanceUntilIdle()
		assertThat(savedData).isEqualTo(entities)
	}

	@Test
	fun given_empty_database_when_fetch_all_then_empty_list_is_emitted() = runTest {
		dao.fetchAllQREntity().test {
			val item = awaitItem()
			advanceUntilIdle()

			assertThat(item).isEmpty()
			cancelAndIgnoreRemainingEvents()
		}
	}

	@Test
	fun given_entity_when_fetch_by_id_then_matching_entity_is_returned() = runTest {
		val entity = createNewEntry(id = 1L)
		dao.insertNewQR(entity)
		advanceUntilIdle()

		assertThat(dao.fetchQREntityById(1L)).isEqualTo(entity)
		assertThat(dao.fetchQREntityById(2L)).isNull()
	}

	@Test
	fun given_entity_when_fetch_by_id_flow_then_matching_entity_is_emitted() = runTest {
		val entity = createNewEntry(id = 1L)
		dao.insertNewQR(entity)
		advanceUntilIdle()

		dao.fetchQREntityByIdFlow(1L).test {
			assertThat(awaitItem()).isEqualTo(entity)
			cancelAndIgnoreRemainingEvents()
		}
	}

	@Test
	fun given_mixed_types_when_fetch_typed_then_only_matching_entities_are_emitted() = runTest {
		val matching = createNewEntry(id = 1L, type = QRDataType.TYPE_URL)
		dao.insertNewQRBulk(listOf(matching, createNewEntry(id = 2L, type = QRDataType.TYPE_TEXT)))
		advanceUntilIdle()

		dao.fetchAllQREntityTyped(QRDataType.TYPE_URL).test {
			assertThat(awaitItem()).isEqualTo(listOf(matching))
			cancelAndIgnoreRemainingEvents()
		}
		dao.fetchAllQREntityTyped(QRDataType.TYPE_EMAIL).test {
			assertThat(awaitItem()).isEmpty()
			cancelAndIgnoreRemainingEvents()
		}
	}

	@Test
	fun given_multiple_entities_when_favorite_updated_then_only_matching_id_changes() = runTest {
		val first = createNewEntry(id = 1L)
		val second = createNewEntry(id = 2L)
		dao.insertNewQRBulk(listOf(first, second))
		dao.updateIsFavWithMatchingId(true, 1L)

		advanceUntilIdle()
		assertThat(dao.fetchQREntityById(1L)).isEqualTo(first.copy(isFavourite = true))
		assertThat(dao.fetchQREntityById(2)).isEqualTo(second)
	}

	@Test
	fun given_entities_when_single_entity_deleted_then_only_that_row_is_removed() = runTest {
		val deleted = createNewEntry(id = 1L)
		val retained = createNewEntry(id = 2L)
		dao.insertNewQRBulk(listOf(deleted, retained))
		dao.deleteQREntity(deleted)
		advanceUntilIdle()

		dao.fetchAllQREntity().test {
			assertThat(awaitItem()).isEqualTo(listOf(retained))
			cancelAndIgnoreRemainingEvents()
		}
	}

	@Test
	fun given_entities_when_bulk_delete_then_only_supplied_rows_are_removed() = runTest {
		dao.fetchAllQREntity().test {
			// perform all the operations
			val deleted = listOf(createNewEntry(id = 1L), createNewEntry(id = 2L))
			val retained = createNewEntry(id = 3L)
			dao.insertNewQRBulk(deleted + retained)
			dao.deleteQREntityBulk(deleted)
			advanceUntilIdle()

			assertThat(awaitItem()).isEqualTo(listOf(retained))
			cancelAndIgnoreRemainingEvents()
		}
	}

	private fun createNewEntry(
		id: Long,
		title: String = "QR $id",
		type: QRDataType = QRDataType.TYPE_TEXT,
	) = QRDataEntity(
		id = id,
		title = title,
		desc = "Description $id",
		content = "Content $id",
		type = type,
		createdAt = LocalDateTime(2024, 1, 2, 3, 4, 5, 678_000_000),
		modifiedAt = LocalDateTime(2024, 2, 3, 4, 5, 6, 789_000_000),
		isFavourite = false,
	)
}