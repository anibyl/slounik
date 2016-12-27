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
import java.util.ArrayList
import javax.inject.Inject

/**
 * skarnik.by website communication.
 *
 * @author Usievaład Kimajeŭ
 * @created 22.12.2015
 */
class Skarnik : DictionarySiteCommunicator<ArticlesCallback>() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.skarnikUrl

	private var requestCount: Int = 0

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		val requests = ArrayList<StringRequest>()

		requests.add(
				getLoadRequest(
						getRBRequestStr(wordToSearch),
						wordToSearch,
						context,
						callback,
						url + " " + context.resources.getString(R.string.skarnik_dictionary_rus_bel)
				)
		)
		requests.add(
				getLoadRequest(
						getBRRequestStr(wordToSearch),
						wordToSearch,
						context,
						callback,
						url + " " + context.resources.getString(R.string.skarnik_dictionary_bel_rus)
				)
		)
		requests.add(
				getLoadRequest(
						getExplanatoryRequestStr(wordToSearch),
						wordToSearch,
						context,
						callback,
						url + " " + context.resources.getString(R.string.skarnik_dictionary_explanatory)
				)
		)

		requestCount = requests.size

		for (request in requests) {
			getQueue(context).add(request)
		}
	}

	override fun loadArticleDescription(article: Article, context: Context, callBack: ArticlesCallback) {
		// Skarnik has no loadable descriptions.
	}

	override fun enabled(): Boolean {
		return preferences.useSkarnik
	}

	override fun parseElement(element: Element?): Article {
		return object : Article(this) {
			override fun fill(): Article {
				description = Html.fromHtml(element?.html())

				return this
			}
		}.fill()
	}

	private fun getLoadRequest(
			requestStr: String,
			wordToSearch: String,
			context: Context,
			callback: ArticlesCallback,
			dictionaryTitle: String
	): StringRequest {
		return StringRequest(requestStr,
				Response.Listener<kotlin.String> { response ->
					object : AsyncTask<Void, Void, Article>() {
						override fun doInBackground(vararg params: Void): Article? {
							val page = Jsoup.parse(response)
							val articleElements = page.select("p#trn")

							if (articleElements.size == 0) {
								return null
							} else {
								val article = parseElement(articleElements.first())
								article.title = wordToSearch
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
				})
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

		builder.scheme("http")
				.authority(url)
				.appendPath("search")
				.appendQueryParameter("lang", language)
				.appendQueryParameter("term", wordToSearch)

		return builder.build().toString()
	}
}
