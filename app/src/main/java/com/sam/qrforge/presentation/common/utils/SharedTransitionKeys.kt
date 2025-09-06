package com.sam.qrforge.presentation.common.utils

object SharedTransitionKeys {
	const val HOME_SCREEN_TO_CREATE_QR_SCREEN = "shared-bounds-home-screen-to-create-qr-screen"

	const val HOME_SCREEN_TO_SCAN_QR_SCREEN = "shared-bounds-home-screen-to-scan-qr-screen"

	const val CREATE_QR_SCREEN_TO_GENERATE_SCREEN =
		"shared-bounds-create-qr-screen-to-generate-screen"

	const val PREVIEW_SCREEN_TO_SAVE_SCREEN = "shared-bonds-preview-screen-to-save-screen"

	const val QR_DETAILS_SCREEN_TO_EDIT_SCREEN = "shared-bounds-details-to-edit-screen"


	fun sharedBoundsToItemDetail(itemId: Long) =
		"shared-bounds-home-screen-to-details-screen-$itemId"

	fun sharedElementQRCodeItemToDetail(itemId: Long) =
		"shared-element-home-screen-to-details-screen-$itemId-qr-code"

	fun sharedBoundsTitleToDetails(itemId: Long) =
		"shared-bounds-home-screen-to-details-screen-$itemId-title"

	fun sharedElementContentTypeCard(itemId: Long) =
		"shared-element-home-screen-to-details-screen-$itemId-content-card"
}