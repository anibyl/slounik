package org.anibyl.slounik.data.network

import android.net.Uri
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
import org.jsoup.select.Elements
import javax.inject.Inject

/**
 * @author Sieva Kimajeŭ
 * @created 2017-01-01
 */
class RodnyjaVobrazy : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.rodnyjaVobrazyUrl

	private var requestCount: Int = 0

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, callback: ArticlesCallback) {
		val requests: List<StringRequest> = arrayListOf(
				getLoadRequest(
						getExplanatoryRequestString(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.rodnyja_vobrazy_dictionary_explanatory)
				),
				getLoadRequest(
						getEthnographyRequestString(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.rodnyja_vobrazy_dictionary_ethnography)
				),
				getLoadRequest(
						getMythologyRequestString(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.rodnyja_vobrazy_dictionary_mythology)
				),
				getLoadRequest(
						getRedListRequestString(wordToSearch),
						wordToSearch,
						callback,
						context.resources.getString(R.string.rodnyja_vobrazy_dictionary_red_list)
				)
		)

		requestCount = requests.size

		for (request in requests) {
			queue.add(request)
		}
	}

	override fun loadArticleDescription(article: Article, callback: ArticlesCallback) {
		val builder = Uri.Builder()

		builder.scheme("http")
				.authority(url)
				.path(article.linkToFullDescription!!.substring(1))

		val requestString = builder.build().toString()

		val request: StringRequest = getArticleDescriptionLoadRequest(requestString, article, callback)

		queue.add(request)
	}

	override fun enabled(): Boolean {
		return preferences.useRodnyjaVobrazy
	}

	override fun parseElement(element: Element, wordToSearch: String?): Article {
		return Article(this).apply {
			val tds: Elements = element.select("td")

			when (tds.size) {
				3 -> {
					// Explanatory.
					title = tds[0].html()
							.replace(oldValue = "<b>", newValue = "", ignoreCase = true)
							.replace(oldValue = "</b>", newValue = "", ignoreCase = true)
							.trim()

					checkTitle(title, wordToSearch)

					description = tds[1].html() + "<br>" + tds[2].html()
				}
				1, 2 -> {
					// Other dictionaries.
					val td = tds.last()

					title = td.select("span").first().ownText().trim()

					checkTitle(title, wordToSearch)

					description = td.html()
					linkToFullDescription = td.select("a").attr("href")
				}
			}
		}
	}

	private fun getLoadRequest(
			requestString: String, wordToSearch: String, callback: ArticlesCallback, dictionaryTitle: String
	): StringRequest {
		return StringRequest(requestString,
			{ response: String? ->
				doAsync {
					val page = Jsoup.parse(response)
					val articleElements = page.select("table")

					val article: Article?
					if (articleElements.size < 1) {
						article = null
					} else {
						article = try {
							parseElement(articleElements.first(), wordToSearch)
						} catch (e: IncorrectTitleException) {
							null
						}

						article?.dictionary = "$dictionaryTitle $url"
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
			},
			{ error: VolleyError ->
				notifier.log("Error response for $requestString: ${error.message}")

				if (--requestCount == 0) {
					callback.invoke(ArticlesInfo(FINISHED))
				}
			}
		)
	}

	private fun getExplanatoryRequestString(wordToSearch: String): String {
		val builder = Uri.Builder()

		builder.scheme("http")
				.authority(url)
				.appendPath("dictionary")
				.appendPath("searchWords")
				.appendQueryParameter("searchKeyword", wordToSearch)

		return builder.build().toString()
	}

	private fun getEthnographyRequestString(wordToSearch: String): String {
		return getRequestString(wordToSearch, 0)
	}

	private fun getMythologyRequestString(wordToSearch: String): String {
		return getRequestString(wordToSearch, 1)
	}

	private fun getRedListRequestString(wordToSearch: String): String {
		return getRequestString(wordToSearch, 2)
	}

	private fun getRequestString(wordToSearch: String, type: Int): String {
		val builder = Uri.Builder()

		builder.scheme("http")
				.authority(url)
				.appendPath("dictionary")
				.appendPath("searchDictionaryWords")
				.appendQueryParameter("type", type.toString())
				.appendQueryParameter("searchKeyword", wordToSearch)

		return builder.build().toString()
	}

	private fun getArticleDescriptionLoadRequest(
			requestString: String, article: Article, callback: ArticlesCallback
	): StringRequest {
		return StringRequest(requestString,
			{ response: String ->
				doAsync {
					notifier.log("Response received for $requestString.")
					val articlePage = Jsoup.parse(response)

					var fullArticleDescription = ""

					val titleTd: Element? = articlePage.select("td[class$=text_title]")?.first()

					if (titleTd != null) {
						var tr: Element? = titleTd.parent()
						do {
							fullArticleDescription += tr!!.html() + "<br>"
							tr = tr.nextElementSibling()
						} while (tr != null)
					}

					article.fullDescription = fullArticleDescription

					val articles = arrayListOf(article)

					uiThread {
						callback.invoke(ArticlesInfo(articles))
					}
				}
			},
			{ error: VolleyError ->
				notifier.log("Error response for $requestString: ${error.message}")
				callback.invoke(ArticlesInfo(FINISHED))
			}
		)
	}

	private fun checkTitle(title: String?, wordToSearch: String?) {
		if (!wordToSearch.equals(title, ignoreCase = true)) {
			throw IncorrectTitleException("Incorrect article title $title for search $wordToSearch.")
		}
	}
}