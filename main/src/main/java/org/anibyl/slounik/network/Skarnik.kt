package org.anibyl.slounik.network

import android.content.Context
import android.net.Uri
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * skarnik.by website communication.
 *
 * @author Usievaład Kimajeŭ
 * @created 22.12.2015
 */
class Skarnik : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.skarnikUrl

	private var requestCount: Int = 0

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		val requests: List<StringRequest> = arrayListOf(
				getLoadRequest(
						getRBRequestStr(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.skarnik_dictionary_rus_bel)
				),
				getLoadRequest(
						getBRRequestStr(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.skarnik_dictionary_bel_rus)
				),
				getLoadRequest(
						getExplanatoryRequestStr(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.skarnik_dictionary_explanatory)
				)
		)

		requestCount = requests.size

		for (request in requests) {
			queue.add(request)
		}
	}

	override fun loadArticleDescription(article: Article, callback: ArticlesCallback) {
		// Skarnik has no loadable descriptions.
	}

	override fun enabled(): Boolean {
		return preferences.useSkarnik
	}

	override fun parseElement(element: Element, wordToSearch: String?): Article {
		return Article(this).apply {
			description = element.html()
		}
	}

	private fun getLoadRequest(
			requestStr: String,
			wordToSearch: String,
			callback: ArticlesCallback,
			dictionaryTitle: String
	): StringRequest {
		return StringRequest(requestStr,
				Response.Listener<kotlin.String> { response ->
					// In the good old days I received correct page immediately but now it is empty page most likely.
					processPage(response, wordToSearch, callback, dictionaryTitle)
				},
				Response.ErrorListener { error ->
					fun loadPage(url: String) {
						queue.add(getPageRequest(url, wordToSearch, callback, dictionaryTitle))
					}

					val networkResponse: NetworkResponse? = error.networkResponse

					if (networkResponse != null && networkResponse.statusCode == 302) {
						/* Skarnik has strange redirection behaviour:
						   http search → https search → http page → https page.
						   I skip first and third parts to get
						   https search → https page. */
						val location: String? = networkResponse.headers["Location"]
						if (location != null) {
							if (location.startsWith("http:")) {
								loadPage(location.replace("http:", "https:"))
							} else {
								// Should not happen.
								loadPage(location)
							}
						} else {
							// Should not happen.
							fail(error, callback)
						}
					} else {
						fail(error, callback)
					}
				})
	}

	private fun getPageRequest(
			requestStr: String, wordToSearch: String, callback: ArticlesCallback, dictionaryTitle: String
	): StringRequest {
		return StringRequest(requestStr,
				Response.Listener<kotlin.String> { response ->
					processPage(response, wordToSearch, callback, dictionaryTitle)
				},
				Response.ErrorListener { error ->
					fail(error, callback)
				}
		)
	}

	private fun processPage(
			response: String, wordToSearch: String, callback: ArticlesCallback, dictionaryTitle: String
	) {
		doAsync {
			val page = Jsoup.parse(response)
			val articleElements = page.select("p#trn")

			val article: Article? = if (articleElements.size == 0) {
				null
			} else {
				parseElement(articleElements.first()).apply {
					this.title = wordToSearch
					this.dictionary = dictionaryTitle
				}
			}

			uiThread {
				val status = if (--requestCount == 0)
					ArticlesInfo.Status.SUCCESS
				else
					ArticlesInfo.Status.IN_PROCESS
				val info: ArticlesInfo = if (article != null) {
					ArticlesInfo(listOf(article), status)
				} else {
					ArticlesInfo(status)
				}
				callback.invoke(info)
			}
		}
	}

	private fun getRBRequestStr(wordToSearch: String): String {
		return getRequestStr(wordToSearch, "rus")
	}

	private fun getBRRequestStr(wordToSearch: String): String {
		return getRequestStr(wordToSearch, "bel")
	}

	private fun getExplanatoryRequestStr(wordToSearch: String): String {
		return getRequestStr(wordToSearch, "beld")
	}

	private fun getRequestStr(wordToSearch: String, language: String): String {
		val builder = Uri.Builder()

		builder.scheme("https")
				.authority(url)
				.appendPath("search")
				.appendQueryParameter("lang", language)
				.appendQueryParameter("term", wordToSearch)

		return builder.build().toString()
	}

	private fun fail(error: VolleyError, callback: ArticlesCallback) {
		notifier.log("Response error: " + error.message)
		val status = if (--requestCount == 0)
			ArticlesInfo.Status.FAILURE
		else
			ArticlesInfo.Status.IN_PROCESS
		callback.invoke(ArticlesInfo(status))
	}
}
