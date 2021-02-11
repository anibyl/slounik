package org.anibyl.slounik.data.network

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.anibyl.slounik.R
import org.anibyl.slounik.getAndroidId
import org.jetbrains.anko.doAsync
import org.json.JSONException
import org.json.JSONObject

/**
 * Own server communication.
 *
 * @author Sieva Kimajeŭ
 * @created 05.04.2015
 */
class Server {
	class Config {
		var isTestDevice: Boolean = false
		var slounikServerUrl: String = "18.222.103.40:8080"
		var slounikOrgUrl: String = "slounik.org"
		var skarnikUrl: String = "skarnik.by"
		var rodnyjaVobrazyUrl: String = "rv-blr.com"
		var verbumUrl: String = "verbum.by"
	}

	fun loadConfig(context: Context): Config {
		val config = Config()

		val androidId = getAndroidId(context)

		val requestString = context.getString(R.string.config)
		val queue = Volley.newRequestQueue(context)
		val request = StringRequest(requestString,
				Response.Listener { response ->
					doAsync {
						try {
							val json = JSONObject(response)

							config.slounikServerUrl= json.getString("slounikServerUrl")

							config.slounikOrgUrl = json.getString("slounikOrgUrl")

							config.skarnikUrl = json.getString("skarnikUrl")

							config.rodnyjaVobrazyUrl = json.getString("rodnyjaVobrazyUrl")

							config.verbumUrl = json.getString("verbumUrl")

							val array = json.getJSONArray("testDevices")

							for (i in 0 until array.length()) {
								if (androidId == array.getJSONObject(i).optString("androidId")) {
									config.isTestDevice = true
									break
								}
							}
						} catch (e: JSONException) {
							Log.e(TAG, "Config cannot be read.", e)
						}
					}
				},
				Response.ErrorListener { error ->
					Log.e(TAG, "Error response for $requestString: ${error.message}")
				})

		queue.add(request)

		return config
	}
}

private const val TAG: String = "Slounik server"
