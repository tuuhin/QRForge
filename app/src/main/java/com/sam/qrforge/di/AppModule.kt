package com.sam.qrforge.di

import com.sam.qrforge.data.provider.ContactsDataProviderImpl
import com.sam.qrforge.data.provider.LocationProviderImpl
import com.sam.qrforge.data.facade.QRGeneratorFacadeImpl
import com.sam.qrforge.domain.provider.ContactsDataProvider
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
	singleOf(::QRGeneratorFacadeImpl).bind<QRGeneratorFacade>()
	singleOf(::LocationProviderImpl).bind<LocationProvider>()
	singleOf(::ContactsDataProviderImpl).bind<ContactsDataProvider>()
}