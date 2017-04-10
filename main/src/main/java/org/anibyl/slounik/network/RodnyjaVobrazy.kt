package org.anibyl.slounik.network

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.text.Html
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author Usievaład Kimajeŭ
 * @created 01.01.2017
 */
class RodnyjaVobrazy : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.rodnyjaVobrazyUrl

	private var requestCount: Int = 0

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		val requests: List<StringRequest> = arrayListOf(
				getLoadRequest(
						getExplanatoryRequestString(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.rodnyja_vobrazy_dictionary_explanatory)
				),
				getLoadRequest(
						getEthnographyRequestString(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.rodnyja_vobrazy_dictionary_ethnography)
				),
				getLoadRequest(
						getMythologyRequestString(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.rodnyja_vobrazy_dictionary_mythology)
				),
				getLoadRequest(
						getRedListRequestString(wordToSearch),
						wordToSearch,
						callback,
						url + " " + context.resources.getString(R.string.rodnyja_vobrazy_dictionary_red_list)
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

					description = Html.fromHtml(tds[1].html() + "<br>" + tds[2].html())
				}
				1, 2 -> {
					// Other dictionaries.
					val td = tds.last()

					title = td.select("span").first().ownText().trim()

					checkTitle(title, wordToSearch)

					description = Html.fromHtml(td.html())
					linkToFullDescription = td.select("a").attr("href")
				}
			}
		}
	}

	private fun getLoadRequest(
			requestString: String,
			wordToSearch: String,
			callback: ArticlesCallback,
			dictionaryTitle: String
	): StringRequest {
		return StringRequest(requestString,
				Response.Listener<String> { response ->
					object : AsyncTask<Void, Void, Article>() {
						override fun doInBackground(vararg params: Void): Article? {
							val page = Jsoup.parse(response)
							val articleElements = page.select("table")

							if (articleElements.size < 1) {
								return null
							} else {
								val article: Article
								try {
									// TODO Add other.
									article = parseElement(articleElements.first(), wordToSearch)
								} catch (e: IncorrectTitleException) {
									return null
								}

								article.dictionary = dictionaryTitle
								return article
							}
						}

						override fun onPostExecute(article: Article?) {
							val status = if (--requestCount == 0)
								ArticlesInfo.Status.SUCCESS
							else
								ArticlesInfo.Status.IN_PROCESS
							val info: ArticlesInfo
							if (article != null) {
								info = ArticlesInfo(object : ArrayList<Article>() {
									init {
										add(article)
									}
								}, status)
							} else {
								info = ArticlesInfo(status)
							}
							callback.invoke(info)
						}
					}.execute()
				},
				Response.ErrorListener {
					notifier.toast("Error response.", developerMode = true)
					// TODO fix it.
					val status = if (--requestCount == 0)
						ArticlesInfo.Status.FAILURE
					else
						ArticlesInfo.Status.IN_PROCESS
					callback.invoke(ArticlesInfo(status))
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
			requestString: String,
			article: Article,
			callback: ArticlesCallback
	): StringRequest {
		return StringRequest(requestString,
				Response.Listener<String> { response ->
					object : AsyncTask<Void, Void, ArrayList<Article>>() {
						override fun doInBackground(vararg params: Void): ArrayList<Article> {
							notifier.log("Response received for $requestString.")
							val articlePage = Jsoup.parse(response)

							var fullArticleDescription: String = ""

							val titleTd: Element? = articlePage.select("td[class$=text_title]")?.first()

							if (titleTd != null) {
								var tr: Element? = titleTd.parent()
								do {
									fullArticleDescription += tr!!.html() + "<br>"
									tr = tr.nextElementSibling()
								} while (tr != null)
							}

							article.fullDescription = Html.fromHtml(fullArticleDescription)

							return arrayListOf(article)
						}

						override fun onPostExecute(articles: ArrayList<Article>) {
							callback.invoke(ArticlesInfo(articles))
						}
					}.execute()
				},
				Response.ErrorListener { error ->
					notifier.log("Response error: " + error.message)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				}
		)
	}

	private fun checkTitle(title: String?, wordToSearch: String?) {
		if (!wordToSearch.equals(title, ignoreCase = true)) {
			throw IncorrectTitleException("Incorrect article title $title for search $wordToSearch.")
		}
	}
}
