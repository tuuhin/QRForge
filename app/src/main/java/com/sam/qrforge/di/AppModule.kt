package com.sam.qrforge.di

import com.sam.qrforge.data.database.QRForgeAppDatabase
import com.sam.qrforge.data.database.dao.QRDataDao
import com.sam.qrforge.data.facade.QRGeneratorFacadeImpl
import com.sam.qrforge.data.facade.SaveGeneratedQRFacadeImpl
import com.sam.qrforge.data.provider.ContactsDataProviderImpl
import com.sam.qrforge.data.provider.LocationProviderImpl
import com.sam.qrforge.data.repository.SaveQRDataRepoImpl
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.facade.SaveGeneratedQRFacade
import com.sam.qrforge.domain.provider.ContactsDataProvider
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {

	// singletons
	single<QRForgeAppDatabase>(createdAtStart = true) {
		QRForgeAppDatabase.createDatabase(androidContext())
	}
	single<QRDataDao> { get<QRForgeAppDatabase>().qrDao() }

	singleOf(::QRGeneratorFacadeImpl).bind<QRGeneratorFacade>()

	// factory
	factoryOf(::LocationProviderImpl).bind<LocationProvider>()
	factoryOf(::ContactsDataProviderImpl).bind<ContactsDataProvider>()
	factoryOf(::SaveQRDataRepoImpl).bind<SavedQRDataRepository>()
	factoryOf(::SaveGeneratedQRFacadeImpl).bind<SaveGeneratedQRFacade>()
}