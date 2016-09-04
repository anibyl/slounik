package org.anibyl.slounik.network

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.text.Html
import android.text.Spanned
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.core.Versioned
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.io.UnsupportedEncodingException
import java.util.ArrayList

/**
 * slounik.org website communication.
 *
 *
 * Created by Usievaład Čorny on 8.4.15 14.17.
 */
class SlounikOrg:DictionarySiteCommunicator() {
	init {
		url = "slounik.org"
	}

	override fun loadArticles(wordToSearch:String, context:Context, callBack:ArticlesCallback) {
		val requestStr:String

		val builder = Uri.Builder()
		builder.scheme("http").authority(url).appendPath("search").appendQueryParameter("search", wordToSearch)

		if (Preferences.searchInTitles) {
			builder.appendQueryParameter("un", "1")
		}

		requestStr = builder.build().toString()

		val request = getLoadRequest(requestStr, context, callBack)

		getQueue(context).add(request)
	}

	override fun loadArticleDescription(article:Article, context:Context, callBack:ArticlesCallback) {
		val builder = Uri.Builder()
		builder.scheme("http").authority(url).appendPath(article.linkToFullDescription!!.substring(1))
		val requestStr = builder.build().toString()

		val request = getArticleDescriptionLoadRequest(requestStr, article, callBack)

		getQueue(context).add(request)
	}

	override fun enabled():Boolean {
		return Preferences.useSlounikOrg
	}

	private fun getLoadRequest(requestStr:String, context:Context, callback:ArticlesCallback):SlounikOrgRequest {
		return SlounikOrgRequest(requestStr,
				Response.Listener<kotlin.String> { response ->
					object:AsyncTask<Void, Void, Void>() {
						private var dicsAmount:Int = 0

						override fun doInBackground(vararg params:Void):Void? {
							val page = Jsoup.parse(response)
							val dicsElements = page.select("a.treeSearchDict")
							dicsAmount = dicsElements.size

							if (dicsAmount == 0) {
								return null
							}

							for (e in dicsElements) {
								var dicRequestStr:String? = e.attr("href")
								if (dicRequestStr != null) {
									val builder = Uri.Builder()
									builder.scheme("http").authority(url).appendEncodedPath(dicRequestStr.substring(1))
									dicRequestStr = builder.build().toString()
									val eachDicRequest = getPerDicLoadingRequest(dicRequestStr,
											object:ArticlesCallback {
												override operator fun invoke(info:ArticlesInfo) {
													setArticleList(info.articles)
												}
											})

									getQueue(context).add(eachDicRequest)
									Notifier.log("Request added to queue: " + eachDicRequest)
								}
							}

							return null
						}

						override fun onPostExecute(ignored:Void?) {
							if (dicsAmount == 0) {
								Notifier.log("Callback invoked: no dictionaries.")
								callback.invoke(ArticlesInfo(articles = null, status = ArticlesInfo.Status.SUCCESS))
							}
						}

						private fun setArticleList(list:ArrayList<Article>?) {
							val status = if (--dicsAmount == 0)
								ArticlesInfo.Status.SUCCESS
							else
								ArticlesInfo.Status.IN_PROCESS
							Notifier.log("Callback invoked, " + (if (list != null) list.size else 0)
									+ " articles added.")
							callback.invoke(ArticlesInfo(list!!, status))
						}
					}.execute()
				},
				Response.ErrorListener {
					Notifier.toast(context, "Error response.", true)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				})
	}

	override fun parseElement(element:Element?):Article {
		return object:Article(this) {
			override fun fill():Article {
				if (element != null) {
					var elements:Elements? = element.select("a.tsb")
					if (elements != null && elements.size != 0) {
						val link = elements.first()
						title = link.html()
						linkToFullDescription = link.attr("href")

						if (title != null) {
							elements = element.select("a.ts")
							if (elements != null && elements.size != 0) {
								description = Html.fromHtml(elements.first().html())
							}
						}
					}

					if (title == null) {
						elements = element.select("b")
						if (elements != null && elements.size != 0) {
							title = elements.first().html()

							if (title != null) {
								description = Html.fromHtml(element.html())
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
						dictionary = elements.first().html()
					}
				}

				return this
			}
		}.fill()
	}

	private fun getPerDicLoadingRequest(dicRequestStr:String, callback:ArticlesCallback):SlounikOrgRequest {
		return SlounikOrgRequest(dicRequestStr,
				Response.Listener<kotlin.String> { response ->
					object:AsyncTask<Void, Void, ArrayList<Article>>() {
						override fun doInBackground(vararg params:Void):ArrayList<Article> {
							Notifier.log("Response received for $dicRequestStr.")
							val dicPage = Jsoup.parse(response)
							val articleElements = dicPage.select("li#li_poszuk")

							var dictionaryTitle:String? = null
							val dictionaryTitles = dicPage.select("a.t3")
							if (dictionaryTitles != null && dictionaryTitles.size != 0) {
								dictionaryTitle = dictionaryTitles.first().html()
							}

							val list = ArrayList<Article>()
							for (e in articleElements) {
								list.add(parseElement(e).apply {
									dictionary = dictionaryTitle
								})
							}

							return list
						}

						override fun onPostExecute(articles:ArrayList<Article>) {
							callback.invoke(ArticlesInfo(articles))
						}
					}.execute()
				},
				Response.ErrorListener { error ->
					Notifier.log("Response error: " + error.message)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				})
	}

	private fun getArticleDescriptionLoadRequest(requestStr:String, article:Article,
			callback:ArticlesCallback):SlounikOrgRequest {
		return SlounikOrgRequest(requestStr,
				Response.Listener<kotlin.String> { response ->
					object:AsyncTask<Void, Void, ArrayList<Article>>() {
						override fun doInBackground(vararg params:Void):ArrayList<Article> {
							Notifier.log("Response received for $requestStr.")
							val articlePage = Jsoup.parse(response)
							val articleElement = articlePage.select("td.n12").first()

							val htmlDescription = Html.fromHtml(articleElement.html())
							val descriptionWithOutExtraSpace = htmlDescription.toString().trim { it <= ' ' }

							article.fullDescription = htmlDescription.subSequence(
									0,
									descriptionWithOutExtraSpace.length
							) as Spanned

							val list = ArrayList<Article>()
							list.add(article)

							return list
						}

						override fun onPostExecute(articles:ArrayList<Article>) {
							callback.invoke(ArticlesInfo(articles))
						}
					}.execute()
				},
				Response.ErrorListener { error ->
					Notifier.log("Response error: " + error.message)
					callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
				})
	}

	private class SlounikOrgRequest(url:String, listener:Response.Listener<String>, errorListener:Response.ErrorListener)
			:StringRequest(url, listener, errorListener) {
		override fun parseNetworkResponse(response:NetworkResponse):Response<String> {
			val parsed:String
			try {
				parsed = String(response.data, charset(HttpHeaderParser.parseCharset(response.headers, Versioned.UTF_8)))
			} catch (e:UnsupportedEncodingException) {
				parsed = String(response.data)
			}

			return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
		}
	}
}