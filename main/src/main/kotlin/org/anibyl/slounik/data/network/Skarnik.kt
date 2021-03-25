package org.anibyl.slounik.data.network

import android.net.Uri
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.ArticlesCallback
import org.anibyl.slounik.data.ArticlesInfo
import org.anibyl.slounik.data.ArticlesInfo.Status.FINISHED
import org.anibyl.slounik.data.ArticlesInfo.Status.IN_PROCESS
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * skarnik.by website communication.
 *
 * @author Sieva Kimajeŭ
 * @created 2015-12-22
 */
class Skarnik : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.skarnikUrl

	private var requestCount: Int = 0

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, callback: ArticlesCallback) {
		val requests: List<StringRequest> = arrayListOf(
				getLoadRequest(
						getRBRequestStr(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.skarnik_dictionary_rus_bel)
				),
				getLoadRequest(
						getBRRequestStr(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.skarnik_dictionary_bel_rus)
				),
				getLoadRequest(
						getExplanatoryRequestStr(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.skarnik_dictionary_explanatory)
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
			requestString: String,
			wordToSearch: String,
			callback: ArticlesCallback,
			dictionaryTitle: String
	): StringRequest {
		val completeDictionaryTitle = "$dictionaryTitle $url"

		return StringRequest(
			requestString,
			{ response: String ->
				// In the good old days I received correct page immediately but now it is empty page most likely.
				processPage(response, wordToSearch, callback, completeDictionaryTitle)
			},
			{ error: VolleyError ->
				notifier.log("Error response for $requestString: ${error.message}")

				fun loadPage(url: String) {
					queue.add(getPageRequest(url, wordToSearch, callback, completeDictionaryTitle))
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
						fail(requestString, error, callback)
					}
				} else {
					fail(requestString, error, callback)
				}
			})
	}

	private fun getPageRequest(
			requestString: String, wordToSearch: String, callback: ArticlesCallback, dictionaryTitle: String
	): StringRequest {
		return StringRequest(
			requestString,
			{ response: String -> processPage(response, wordToSearch, callback, dictionaryTitle) },
			{ error: VolleyError -> fail(requestString, error, callback) }
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
				if (--requestCount == 0) {
					callback.invoke(ArticlesInfo(article, FINISHED))
				} else {
					if (article != null) {
						callback.invoke(ArticlesInfo(article, IN_PROCESS))
					}
				}
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

	private fun fail(requestString: String, error: VolleyError, callback: ArticlesCallback) {
		notifier.log("Error response for $requestString: ${error.message}")

		if (--requestCount == 0) {
			callback.invoke(ArticlesInfo(FINISHED))
		}
	}
}
