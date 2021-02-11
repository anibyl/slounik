package org.anibyl.slounik

import android.content.Context
import android.provider.Settings.Secure

/**
 * General application utility.
 *
 * @author Sieva Kimajeŭ
 * @created 05.04.2015
 */

/**
 * Retrieves Android identifier from Settings.Secure.
 */
fun getAndroidId(context:Context):String {
	return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
}
