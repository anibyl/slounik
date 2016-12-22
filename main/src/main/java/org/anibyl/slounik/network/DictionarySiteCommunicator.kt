package org.anibyl.slounik.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.jsoup.nodes.Element

/**
 * Dictionary site communicator.
 *
 * @author Usievaład Kimajeŭ
 * @created 22.12.2015
 */
abstract class DictionarySiteCommunicator<in T> : ArticlesLoader<T> where T : ArticlesCallback {
	var url: String? = "slounik.org"
	private var queue: RequestQueue? = null

	abstract fun loadArticleDescription(article: Article, context: Context, callBack: T)
	abstract fun enabled(): Boolean

	protected abstract fun parseElement(element: Element?): Article

	protected fun getQueue(context: Context): RequestQueue {
		if (queue == null) {
			queue = Volley.newRequestQueue(context)
		}

		return queue!!
	}
}
