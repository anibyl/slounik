package org.anibyl.slounik.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.jsoup.nodes.Element

/**
 * Dictionary site communicator.
 *
 * Created by Usievaład Čorny on 22.12.15.
 */
abstract class DictionarySiteCommunicator:ArticlesLoader {
	var url:String? = "slounik.org"
	private var queue:RequestQueue? = null

	abstract fun loadArticleDescription(article:Article, context:Context, callBack:ArticlesCallback)
	abstract fun enabled():Boolean

	protected abstract fun parseElement(element:Element?):Article

	protected fun getQueue(context:Context):RequestQueue {
		if (queue == null) {
			queue = Volley.newRequestQueue(context)
		}

		return queue!!
	}
}