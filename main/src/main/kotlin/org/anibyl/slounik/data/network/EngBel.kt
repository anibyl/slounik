package org.anibyl.slounik.data.network

import android.content.Context
import android.net.Uri
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.ArticlesCallback
import org.anibyl.slounik.data.ArticlesInfo
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * @author Sieva Kimajeŭ
 * @created 2018-07-02
 */
@Deprecated("Slounik server is deprecated.")
class EngBel : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.slounikServerUrl

	init {
//		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		queue.add(
				getLoadRequest(
						getRequestUrl(wordToSearch),
						callback,
						context.resources.getString(R.string.db_engbel)
				)
		)
	}

	override fun loadArticleDescription(article: Article, callback: ArticlesCallback) {
		// Slounik server has no loadable description.
	}

	override fun enabled(): Boolean {
//		return preferences.useSlounikServer
		return false
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
				Response.Listener { response ->
					doAsync {
						val articles: List<Article> = Gson().fromJson(response, EngBelResponse::class.java)
						articles.forEach { article: Article -> article.dictionary = dictionaryTitle }

						uiThread {
							callback.invoke(ArticlesInfo(articles))
						}
					}
				},
				Response.ErrorListener { error ->
					notifier.log("Error response for $requestString: ${error.message}")
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
