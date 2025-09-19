package com.sam.qrforge.data.provider

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.sam.qrforge.MainActivity
import com.sam.qrforge.R
import com.sam.qrforge.domain.provider.AppShortcutProvider
import com.sam.qrforge.presentation.navigation.nav_graph.NavDeepLinks

class ShortcutsProviderImpl(private val context: Context) : AppShortcutProvider {

	private val createShortCut: ShortcutInfoCompat
		get() = ShortcutInfoCompat.Builder(context, "create_new_qr")
			.setShortLabel(context.getString(R.string.shortcut_short_label_generate_qr))
			.setLongLabel(context.getString(R.string.shortcut_label_generate_qr))
			.setIcon(IconCompat.createWithResource(context, R.drawable.ic_qr_simplified))
			.setIntent(
				Intent().apply {
					action = Intent.ACTION_VIEW
					data = NavDeepLinks.CREATE_NEW_QR_DEEP_LINK.toUri()
					setClass(context, MainActivity::class.java)
				}
			)
			.build()

	private val scanShortCut: ShortcutInfoCompat
		get() = ShortcutInfoCompat.Builder(context, "scan_qr")
			.setShortLabel(context.getString(R.string.shortcut_short_label_scan_qr))
			.setLongLabel(context.getString(R.string.shortcut_label_scan_qr))
			.setIcon(IconCompat.createWithResource(context, R.drawable.ic_scan))
			.setIntent(
				Intent().apply {
					action = Intent.ACTION_VIEW
					data = NavDeepLinks.SCAN_QR_DEEP_LINK.toUri()
					setClass(context, MainActivity::class.java)
				}
			)
			.build()

	override fun setShortcuts() {
		ShortcutManagerCompat.setDynamicShortcuts(
			context,
			listOf(createShortCut, scanShortCut)
		)
	}

}