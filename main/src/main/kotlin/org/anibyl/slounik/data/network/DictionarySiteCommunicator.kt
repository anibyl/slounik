package org.anibyl.slounik.data.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.ArticlesCallback
import org.anibyl.slounik.data.ArticlesLoader
import org.anibyl.slounik.data.FullDescriptionLoader
import org.anibyl.slounik.data.network.Server.Config
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * Dictionary site communicator.
 *
 * @author Sieva Kimajeŭ
 * @created 22.12.2015
 */
abstract class DictionarySiteCommunicator : ArticlesLoader<ArticlesCallback>, FullDescriptionLoader {
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

	protected abstract fun parseElement(element: Element, wordToSearch: String? = null): Article
}
