package org.anibyl.slounik.core

import android.os.Build

/**
 * Contains Android version related stuff.
 *
 * @author Usievaład Kimajeŭ
 * @created 17.07.2016
 */
fun version(version: Int, old: () -> Unit, new: () -> Unit) {
	if (Build.VERSION.SDK_INT < version) {
		old()
	} else {
		new()
	}
}
