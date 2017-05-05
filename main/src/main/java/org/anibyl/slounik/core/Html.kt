package org.anibyl.slounik.core

import android.os.Build.VERSION_CODES.N
import android.text.Html
import android.text.Spanned

/**
 * @author Usievaład Kimajeŭ
 * @created 05.05.2017
 */

fun String?.fromHtml(): Spanned {
	return versionResult(
			N,
			{
				Html.fromHtml(this)
			},
			{
				Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
			}
	)
}
