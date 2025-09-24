package com.sam.qrforge.di

import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.QRValidatorFacade
import com.sam.qrforge.presentation.feature_create.CreateNewQRViewModel
import com.sam.qrforge.presentation.feature_detail.QRDetailsViewModel
import com.sam.qrforge.presentation.feature_export.ExportQRViewModel
import com.sam.qrforge.presentation.feature_home.HomeViewModel
import com.sam.qrforge.presentation.feature_scan.viewmodel.CameraController
import com.sam.qrforge.presentation.feature_scan.viewmodel.CameraViewModel
import com.sam.qrforge.presentation.feature_scan.viewmodel.ScanQRViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule = module {
	viewModelOf(::HomeViewModel)
	viewModelOf(::CreateNewQRViewModel)
	viewModelOf(::QRDetailsViewModel)
	viewModelOf(::ScanQRViewModel)

	viewModel {
		ExportQRViewModel(
			get<FileStorageFacade>(),
			get<QRValidatorFacade>(named("ML_KIT"))
		)
	}

	factoryOf(::CameraController)
	viewModelOf(::CameraViewModel)
}