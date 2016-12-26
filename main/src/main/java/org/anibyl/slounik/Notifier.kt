package org.anibyl.slounik

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.anibyl.slounik.network.Server

/**
 * Application notifier.
 *
 * @author Usievaład Kimajeŭ
 * @created 05.04.2015
 */
object Notifier {
	fun toast(context: Context, id: Int) {
		toast(context, context.resources.getString(id), false)
	}

	fun toast(
			context: Context,
			text: CharSequence,
			developerMode: Boolean = false,
			length: Int = Toast.LENGTH_SHORT
	) {
		if (!developerMode || Server.isTestDevice) {
			Toast.makeText(context, text, length).show()
		}
	}

	fun log(message: String) {
		if (Server.isTestDevice) {
			Log.d("Slounik", message)
		}
	}
}
