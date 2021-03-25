package org.anibyl.slounik.data.network

import android.net.Uri
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonParser
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.ArticlesCallback
import org.anibyl.slounik.data.ArticlesInfo
import org.anibyl.slounik.data.ArticlesInfo.Status.FINISHED
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject


/**
 * verbum.by website communication.
 *
 * @author Sieva Kimajeŭ
 * @created 2021-02-10
 */
class Verbum : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.verbumUrl

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, callback: ArticlesCallback) {
		queue.add(
			getLoadRequest(
				Uri.Builder()
					.scheme("https")
					.authority(url)
					.appendPath("api")
					.appendPath("search")
					.appendQueryParameter("q", wordToSearch)
					.build()
					.toString(),
				wordToSearch,
				callback
			)
		)
	}

	override fun loadArticleDescription(article: Article, callback: ArticlesCallback) {
		// Verbum has no loadable descriptions.
	}

	override fun enabled(): Boolean {
		return preferences.useVerbum
	}

	override fun parseElement(element: Element, wordToSearch: String?): Article {
		return Article(this).apply {
			description = element.html()
		}
	}

	private fun getLoadRequest(requestString: String, wordToSearch: String, callback: ArticlesCallback): StringRequest {
		return StringRequest(requestString,
			{ response ->
				processPage(response, wordToSearch, callback)
			},
			{ error ->
				notifier.log("Error response for $requestString: ${error.message}")
				callback.invoke(ArticlesInfo(FINISHED))
			}
		)
	}

	private fun processPage(response: String, wordToSearch: String, callback: ArticlesCallback) {
		doAsync {
			val articles = try {
				JsonParser.parseString(response)
					.asJsonObject
					.get("Articles")
					.asJsonArray
					.map { e -> e.asJsonObject }
					.mapNotNull { e ->
						val description = e.get("Content").asString

						val parsedDescription: Document = Jsoup.parse(description)

						val vhws: Elements = parsedDescription.select("v-hw")

						val title = if (vhws.isEmpty()) {
							val ml0s: Elements = parsedDescription.select("p.ml-0")

							if (ml0s.isEmpty()) "" else ml0s[0].text()
						} else {
							vhws[0].text()
						}.substringBefore(',')

						if (preferences.searchInTitles && !title.contains(wordToSearch)) {
							return@mapNotNull null
						}

						val dictionary = when (e.get("DictionaryID").asString) {
							"bel-rus" -> context.resources.getString(R.string.verbum_bel_rus)
							"krapiva" -> context.resources.getString(R.string.verbum_explanatory_krapiva)
							"pashkievich" -> context.resources.getString(R.string.verbum_eng_bel)
							"rus-bel" -> context.resources.getString(R.string.verbum_rus_bel)
							"rvblr" -> context.resources.getString(R.string.verbum_explanatory_rvblr)
							else -> ""
						}

						Article().apply {
							this.title = title
							this.description = description
							this.dictionary = "$dictionary $url"
						}
					}
			} catch (t: Throwable) {
				notifier.log("Unable to parse Verbum response: ${t.message}")
				emptyList()
			}

			uiThread {
				callback.invoke(ArticlesInfo(articles))
			}
		}
	}
}
