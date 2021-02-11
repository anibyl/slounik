package org.anibyl.slounik

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.anibyl.slounik.data.network.Server.Config
import javax.inject.Inject

/**
 * Application notifier.
 *
 * @author Sieva Kimajeŭ
 * @created 05.04.2015
 */
class Notifier {
	@Inject lateinit var context: Context
	@Inject lateinit var config: Config

	private val isTestDevice: Boolean
		get() = config.isTestDevice

	init {
		SlounikApplication.graph.inject(this)
	}

	fun toast(id: Int) {
		toast(context.resources.getString(id), false)
	}

	fun toast(text: CharSequence, developerMode: Boolean = false, length: Int = Toast.LENGTH_SHORT) {
		if (!developerMode || isTestDevice) {
			Toast.makeText(context, text, length).show()
		}
	}

	fun log(message: String) {
		if (isTestDevice) {
			Log.d("Slounik", message)
		}
	}
}
