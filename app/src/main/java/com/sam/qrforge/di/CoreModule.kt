package com.sam.qrforge.di

import android.content.Context
import com.sam.qrforge.data.database.QRForgeAppDatabase
import com.sam.qrforge.data.database.dao.QRDataDao
import com.sam.qrforge.data.facade.FileStorageFacadeImpl
import com.sam.qrforge.data.facade.QRGeneratorFacadeImpl
import com.sam.qrforge.data.facade.ZxingQRValidatorFacadeImpl
import com.sam.qrforge.data.provider.ContactsDataProviderImpl
import com.sam.qrforge.data.provider.LocationProviderImpl
import com.sam.qrforge.data.provider.ShortcutsProviderImpl
import com.sam.qrforge.data.provider.WIFIConnectionProviderImpl
import com.sam.qrforge.data.repository.SaveQRDataRepoImpl
import com.sam.qrforge.data.utils.PlatformCoroutineDispatcherImpl
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.facade.QRValidatorFacade
import com.sam.qrforge.domain.provider.AppShortcutProvider
import com.sam.qrforge.domain.provider.ContactsDataProvider
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.domain.provider.WIFIConnectionProvider
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.domain.util.PlatformCoroutineDispatchers
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
class CoreModule {

    @Single
    fun providesDispatchers(): PlatformCoroutineDispatchers = PlatformCoroutineDispatcherImpl()

    @Single(createdAtStart = true)
    fun providesAppDb(context: Context): QRForgeAppDatabase {
        return QRForgeAppDatabase.createDatabase(context)
    }

    @Single
    fun providesDao(db: QRForgeAppDatabase): QRDataDao = db.qrDao()

    @Single
    fun providesQRGenerator(dispatchers: PlatformCoroutineDispatchers): QRGeneratorFacade =
        QRGeneratorFacadeImpl(dispatchers)

    @Single
    @Named("ZXING")
    fun providesZingValidatorFacade(dispatchers: PlatformCoroutineDispatchers): QRValidatorFacade =
        ZxingQRValidatorFacadeImpl(dispatchers)

    @Single
    fun providesShortcutsProvider(context: Context): AppShortcutProvider =
        ShortcutsProviderImpl(context)

    @Factory
    fun providesLocationProvider(context: Context): LocationProvider = LocationProviderImpl(context)

    @Factory
    fun providesContactsDataProvider(
        context: Context,
        dispatchers: PlatformCoroutineDispatchers,
    ): ContactsDataProvider = ContactsDataProviderImpl(context, dispatchers)

    @Factory
    fun providesSaveQRRepo(dao: QRDataDao): SavedQRDataRepository = SaveQRDataRepoImpl(dao)

    @Factory
    fun providesFileStorageFacade(
        context: Context,
        dispatchers: PlatformCoroutineDispatchers,
    ): FileStorageFacade = FileStorageFacadeImpl(context, dispatchers)


    @Factory
    fun providesWifiConnector(context: Context): WIFIConnectionProvider =
        WIFIConnectionProviderImpl(context)

}
