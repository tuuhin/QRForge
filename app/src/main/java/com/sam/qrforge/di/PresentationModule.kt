package com.sam.qrforge.di

import com.sam.qrforge.presentation.feature_create.CreateNewQRViewModel
import com.sam.qrforge.presentation.feature_detail.QRDetailsViewModel
import com.sam.qrforge.presentation.feature_export.ExportQRViewModel
import com.sam.qrforge.presentation.feature_home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
	viewModelOf(::HomeViewModel)
	viewModelOf(::CreateNewQRViewModel)
	viewModelOf(::ExportQRViewModel)
	viewModelOf(::QRDetailsViewModel)
}