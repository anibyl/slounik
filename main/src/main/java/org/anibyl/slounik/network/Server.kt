package org.anibyl.slounik.network

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
class Server {
	class Config {
		var isTestDevice: Boolean = false
		var slounikOrgUrl: String = "slounik.org"
		var skarnikUrl: String = "skarnik.by"
	}

	fun loadConfig(context: Context): Config {
		val config = Config()

		val androidId = getAndroidId(context)

		val requestStr = context.getString(R.string.server) + "config"
		val queue = Volley.newRequestQueue(context)
		val request = StringRequest(requestStr,
				Response.Listener<String> { response ->
					object : AsyncTask<String, Void, Config>() {
						override fun doInBackground(vararg params: String): Config {
							try {
								val json = JSONObject(response)

								config.slounikOrgUrl = json.getString("slounikOrgUrl")

								config.skarnikUrl = json.getString("skarnikUrl")

								val array = json.getJSONArray("testDevices")

								for (i in 0..array.length() - 1) {
									if (androidId == array.getJSONObject(i).optString("androidId")) {
										config.isTestDevice = true
										break
									}
								}
							} catch (e: JSONException) {
								Log.e(TAG, "Config cannot be read.", e)
							}

							return config
						}
					}.execute()
				},
				Response.ErrorListener {
					Log.e(TAG, "Config cannot be loaded.")
				})

		queue.add(request)

		return config
	}
}

private const val TAG: String = "Slounik server"
