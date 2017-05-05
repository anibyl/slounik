package org.anibyl.slounik.core

import android.os.Build.VERSION.SDK_INT

/**
 * Contains Android version related stuff.
 *
 * @author Usievaład Kimajeŭ
 * @created 17.07.2016
 */
fun version(version: Int, old: () -> Unit, new: () -> Unit) {
	if (SDK_INT < version) {
		old()
	} else {
		new()
	}
}

fun <T> versionResult(version: Int, old: () -> T, new: () -> T): T {
	if (SDK_INT < version) {
		return old()
	} else {
		return new()
	}
}
