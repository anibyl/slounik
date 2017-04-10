package org.anibyl.slounik.core

import android.content.ClipData
import android.content.Context
import android.os.Build.VERSION_CODES.HONEYCOMB

/**
 * @author Usievaład Kimajeŭ
 * @created 05.01.2017
 */

fun Context.copyToClipboard(text: CharSequence) {
	version(HONEYCOMB,
			{
				val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
				clipboard.text = text
			},
			{
				val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
				val clip = ClipData.newPlainText("Article description", text)
				clipboard.primaryClip = clip
			}
	)
}
