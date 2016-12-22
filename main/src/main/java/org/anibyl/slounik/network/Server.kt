package org.anibyl.slounik.network

import android.content.Context
import android.os.AsyncTask
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.getAndroidId
import org.json.JSONException
import org.json.JSONObject

/**
 * Own server communication.
 *
 * @author Usievaład Kimajeŭ
 * @created 05.04.2015
 */
object Server {
	private var config: Config? = null

	abstract class Callback {
		abstract operator fun invoke()
	}

	@JvmOverloads fun loadConfig(context: Context, callback: Callback? = null) {
		val androidId = getAndroidId(context)

		val requestStr = context.getString(R.string.server) + "config"
		val queue = Volley.newRequestQueue(context)
		val request = StringRequest(requestStr,
				Response.Listener<kotlin.String> { response ->
					object : AsyncTask<String, Void, Config>() {
						override fun doInBackground(vararg params: String): Config {
							val config = Config()
							try {
								val json = JSONObject(response)

								val mainUrl = json.getString("mainUrl")
								config.mainUrl = mainUrl

								val skarnikUrl = json.getString("skarnikUrl")
								config.skarnikUrl = skarnikUrl

								val array = json.getJSONArray("testDevices")
								var device: JSONObject
								for (i in 0..array.length() - 1) {
									device = array.getJSONObject(i)
									if (androidId == device.optString("androidId")) {
										config.isTestDevice = true
									}
								}
							} catch (ignored: JSONException) {
								Notifier.log("Config can not be read.")
							}

							return config
						}

						override fun onPostExecute(config: Config) {
							Server.config = config
							callback?.invoke()
						}
					}.execute()
				},
				Response.ErrorListener {
					callback?.invoke()
				})

		queue.add(request)
	}

	val mainUrl: String?
		get() {
			if (config != null) {
				return config!!.mainUrl
			} else {
				return null
			}
		}

	val skarnikUrl: String?
		get() {
			if (config != null) {
				return config!!.skarnikUrl
			} else {
				return null
			}
		}

	val isTestDevice: Boolean
		get() = config != null && config!!.isTestDevice

	private class Config {
		var isTestDevice: Boolean = false
		var mainUrl: String? = null
		var skarnikUrl: String? = null
	}
}
