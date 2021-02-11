package org.anibyl.slounik

import android.content.Context
import android.provider.Settings.Secure

/**
 * General application utility.
 *
 * @author Sieva Kimajeŭ
 * @created 2015-04-05
 */

/**
 * Retrieves Android identifier from Settings.Secure.
 */
fun getAndroidId(context:Context):String {
	return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
}
