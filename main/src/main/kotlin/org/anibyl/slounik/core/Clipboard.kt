package org.anibyl.slounik.core

import android.content.ClipData
import android.content.Context

/**
 * @author Sieva Kimajeŭ
 * @created 2017-01-05
 */

fun Context.copyToClipboard(text: CharSequence) {
	val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
	val clip = ClipData.newPlainText("Article description", text)
	clipboard.setPrimaryClip(clip)
}
