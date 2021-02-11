package org.anibyl.slounik.core

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.text.Html
import android.text.Spanned
import android.text.SpannedString

/**
 * @author Sieva Kimajeŭ
 * @created 05.05.2017
 */

private val htmlLinkRegex = "</?a[^>]*>".toRegex()

fun String?.fromHtml(): Spanned {
	if (this == null) {
		return SpannedString("")
	}

	var result: String = this

	result = result.replace(htmlLinkRegex, "")

	return if (SDK_INT < N) {
		Html.fromHtml(result)
	} else {
		Html.fromHtml(result, Html.FROM_HTML_MODE_LEGACY)
	}
}
