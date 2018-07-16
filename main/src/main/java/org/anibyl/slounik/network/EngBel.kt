package org.anibyl.slounik.network

import android.content.Context
import android.net.Uri
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * @author Usievaład Kimajeŭ
 * @created 02.07.2018
 */
class EngBel : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.slounikServerUrl

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		queue.add(
				getLoadRequest(
						getRequestUrl(wordToSearch),
						callback,
						context.resources.getString(R.string.slounik_server_eng_bel)
				)
		)
	}

	override fun loadArticleDescription(article: Article, callback: ArticlesCallback) {
		// Slounik server has no loadable description.
	}

	override fun enabled(): Boolean {
		return preferences.useSlounikServer
	}

	override fun parseElement(element: Element, wordToSearch: String?): Article {
		// TODO refactor.
		// Redundant.
		return Article(this)
	}

	private fun getLoadRequest(
			requestString: String, callback: ArticlesCallback, dictionaryTitle: String
	): StringRequest {
		return StringRequest(requestString,
				Response.Listener<String> { response ->
					doAsync {
						val articles: List<Article> = Gson().fromJson(response, EngBelResponse::class.java)
						articles.forEach { article: Article -> article.dictionary = dictionaryTitle }

						uiThread {
							callback.invoke(ArticlesInfo(articles))
						}
					}
				},
				Response.ErrorListener {
					notifier.toast("Error response.", developerMode = true)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				}
		)
	}

	private fun getRequestUrl(wordToSearch: String): String {
		val builder = Uri.Builder()

		builder.scheme("http")
				.encodedAuthority(url)
				.appendPath("engbel")
				.appendPath(wordToSearch)
				.appendQueryParameter("d", (!preferences.searchInTitles).toString())

		return builder.build().toString()
	}
}

class EngBelResponse : ArrayList<Article>()
