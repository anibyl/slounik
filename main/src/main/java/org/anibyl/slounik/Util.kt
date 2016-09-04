package org.anibyl.slounik

import android.content.Context
import android.provider.Settings

/**
 * General application utility.
 *
 * Created by Usievaład Čorny on 05.04.2015 4:13.
 */

/**
 * Retrieves Android identifier from Settings.Secure.
 */
fun getAndroidId(context:Context):String {
	return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
