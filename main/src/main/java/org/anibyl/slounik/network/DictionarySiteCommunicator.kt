package org.anibyl.slounik.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.network.Server.Config
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * Dictionary site communicator.
 *
 * @author Usievaład Kimajeŭ
 * @created 22.12.2015
 */
abstract class DictionarySiteCommunicator<in T>() : ArticlesLoader<T> where T : ArticlesCallback {
	@Inject lateinit var config: Config
	@Inject lateinit var preferences: Preferences

	abstract protected val url: String

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
