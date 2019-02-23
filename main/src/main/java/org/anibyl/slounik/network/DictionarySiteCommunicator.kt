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
abstract class DictionarySiteCommunicator : ArticlesLoader<ArticlesCallback> {
	@Inject lateinit var config: Config
	@Inject lateinit var preferences: Preferences
	@Inject lateinit var context: Context

	protected val queue: RequestQueue
		get() {
			if (_queue == null) {
				_queue = Volley.newRequestQueue(context)
			}

			return _queue!!
		}

	protected abstract val url: String

	private var _queue: RequestQueue? = null

	abstract fun loadArticleDescription(article: Article, callback: ArticlesCallback)
	abstract fun enabled(): Boolean

	protected abstract fun parseElement(element: Element, wordToSearch: String? = null): Article
}
