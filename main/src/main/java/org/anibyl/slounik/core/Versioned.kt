package org.anibyl.slounik.core

import android.os.Build

/**
 * Contains Android version related stuff.
 *
 * @author Usievaład Kimajeŭ
 * @created 17.07.2016
 */
object Versioned {
	val UTF_8: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		java.nio.charset.StandardCharsets.UTF_8.name()
	else
		"UTF-8"
}
