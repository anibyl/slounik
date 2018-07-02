package org.anibyl.slounik.network

import android.content.Context
import android.net.Uri
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.SlounikApplication
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject

/**
 * slounik.org website communication.
 *
 * @author Usievaład Kimajeŭ
 * @created 08.04.2015
 */
class SlounikOrg : DictionarySiteCommunicator() {
	@Inject lateinit var notifier: Notifier

	override val url: String
		get() = config.slounikOrgUrl

	init {
		SlounikApplication.graph.inject(this)
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		val requestStr: String

		val builder = Uri.Builder()
		builder.scheme("http").authority(url).appendPath("search").appendQueryParameter("search", wordToSearch)

		if (preferences.searchInTitles) {
			builder.appendQueryParameter("un", "1")
		}

		requestStr = builder.build().toString()

		val request = getLoadRequest(requestStr, callback)

		queue.add(request)
	}

	override fun loadArticleDescription(article: Article, callback: ArticlesCallback) {
		val builder = Uri.Builder()
		builder.scheme("http").authority(url).appendPath(article.linkToFullDescription!!.substring(1))
		val requestStr = builder.build().toString()

		val request = getArticleDescriptionLoadRequest(requestStr, article, callback)

		queue.add(request)
	}

	override fun enabled(): Boolean {
		return preferences.useSlounikOrg
	}

	private fun getLoadRequest(requestStr: String, callback: ArticlesCallback): SlounikOrgRequest {
		return SlounikOrgRequest(requestStr,
				Response.Listener { response ->
					doAsync {
						val page = Jsoup.parse(response)
						val dicsElements = page.select("a.treeSearchDict")
						var dicsAmount: Int = dicsElements.size

						if (dicsAmount != 0) {
							for (e in dicsElements) {
								var dicRequestStr: String? = e.attr("href")
								if (dicRequestStr != null) {
									val builder = Uri.Builder()
									builder.scheme("http").authority(url).appendEncodedPath(dicRequestStr.substring(1))
									dicRequestStr = builder.build().toString()
									val eachDicRequest = getPerDicLoadingRequest(dicRequestStr,
											object : ArticlesCallback {
												override operator fun invoke(info: ArticlesInfo) {
													val status = if (--dicsAmount == 0)
														ArticlesInfo.Status.SUCCESS
													else
														ArticlesInfo.Status.IN_PROCESS
													notifier.log("Callback invoked, " + (info.articles?.size
															?: 0) + " articles added.")
													callback.invoke(ArticlesInfo(info.articles, status))
												}
											})

									queue.add(eachDicRequest)
									notifier.log("Request added to queue: $eachDicRequest")
								}
							}
						}

						uiThread {
							if (dicsAmount == 0) {
								notifier.log("Callback invoked: no dictionaries.")
								callback.invoke(ArticlesInfo(articles = null, status = ArticlesInfo.Status.SUCCESS))
							}
						}
					}
				},
				Response.ErrorListener {
					notifier.toast("Error response.", true)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				})
	}

	override fun parseElement(element: Element, wordToSearch: String?): Article {
		return Article(this).apply {
			var elements: Elements? = element.select("a.tsb")
			if (elements != null && elements.size != 0) {
				val link = elements.first()
				title = link.html()
				linkToFullDescription = link.attr("href")

				if (title != null) {
					elements = element.select("a.ts")
					if (elements != null && elements.size != 0) {
						description = elements.first().html()
					}
				}
			}

			if (title == null) {
				elements = element.select("b")
				if (elements != null && elements.size != 0) {
					title = elements.first().html()

					if (title != null) {
						description = element.html()
					}
				}
			}

			if (title != null) {
				title = title!!.replace("<u>".toRegex(), "")
				title = title!!.replace("</u>".toRegex(), "́")

				// Escape all other HTML tags, e.g. second <b>.
				title = Jsoup.parse(title).text()
			}

			elements = element.select("a.la1")
			if (elements != null && elements.size != 0) {
				dictionary = "$elements.first().html() $url"
			}
		}
	}

	private fun getPerDicLoadingRequest(dicRequestStr: String, callback: ArticlesCallback): SlounikOrgRequest {
		return SlounikOrgRequest(dicRequestStr,
				Response.Listener { response ->
					doAsync {
						notifier.log("Response received for $dicRequestStr.")
						val dicPage = Jsoup.parse(response)
						val articleElements = dicPage.select("li#li_poszuk")

						var dictionaryTitle: String? = null
						val dictionaryTitles = dicPage.select("a.t3")
						if (dictionaryTitles != null && dictionaryTitles.size != 0) {
							dictionaryTitle = dictionaryTitles.first().html()
						}

						val list: List<Article> = articleElements.map {
							e -> parseElement(e).apply { dictionary = "$dictionaryTitle $url" }
						}

						uiThread {
							callback.invoke(ArticlesInfo(list))
						}
					}
				},
				Response.ErrorListener { error ->
					notifier.log("Response error: " + error.message)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				})
	}

	private fun getArticleDescriptionLoadRequest(
			requestStr: String,
			article: Article,
			callback: ArticlesCallback
	): SlounikOrgRequest {
		return SlounikOrgRequest(requestStr,
				Response.Listener { response ->
					doAsync {
						notifier.log("Response received for $requestStr.")
						val articlePage: Document = Jsoup.parse(response)
						val articleElement: Element = articlePage.select("td.n12").first()

						val htmlDescription:String = articleElement.html()
						val descriptionWithOutExtraSpace: String = htmlDescription.trim { it <= ' ' }

						article.fullDescription = htmlDescription.subSequence(0, descriptionWithOutExtraSpace.length)
								as String

						val list = listOf(article)

						uiThread {
							callback.invoke(ArticlesInfo(list))
						}
					}
				},
				Response.ErrorListener { error ->
					notifier.log("Response error: " + error.message)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				})
	}

	private class SlounikOrgRequest(
			url: String,
			listener: Response.Listener<String>,
			errorListener: Response.ErrorListener
	) : StringRequest(url, listener, errorListener) {
		override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
			val parsed = String(response.data, Charsets.UTF_8)

			return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
		}
	}
}
