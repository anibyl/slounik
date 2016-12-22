package org.anibyl.slounik.network

import android.content.Context
import java.util.ArrayList

/**
 * Batch loader of the dictionary site communicators.
 *
 * @author Usievaład Kimajeŭ
 * @created 23.12.2015
 */
class BatchArticlesLoader(vararg communicators: DictionarySiteCommunicator<ArticlesCallback>)
	: ArticlesLoader<BatchArticlesLoader.BatchArticlesCallback> {
	private val communicators: Array<out DictionarySiteCommunicator<ArticlesCallback>>

	init {
		this.communicators = communicators
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: BatchArticlesCallback) {
		var activeCommunicators = 0

		for (communicator in communicators) {
			if (communicator.enabled()) {
				activeCommunicators++
				val articlesCallback = object : ArticlesCallback {
					override fun invoke(info: ArticlesInfo) {
						callback.invoke(this, info)
					}
				}
				callback.addCallback(articlesCallback)
				communicator.loadArticles(wordToSearch, context, articlesCallback)
			}
		}

		if (activeCommunicators == 0) {
			callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
		}
	}

	abstract class BatchArticlesCallback : ArticlesCallback {
		internal var callbacks = ArrayList<ArticlesCallback>()

		internal operator fun invoke(callback: ArticlesCallback, info: ArticlesInfo) {
			if (!callbacks.contains(callback)) {
				throw RuntimeException("No such callback in batch callback.")
			}

			when (info.status) {
				ArticlesInfo.Status.SUCCESS, ArticlesInfo.Status.FAILURE -> {
					callbacks.remove(callback)

					if (callbacks.size != 0) {
						info.status = ArticlesInfo.Status.IN_PROCESS
					}
					invoke(info)
				}

				ArticlesInfo.Status.IN_PROCESS -> invoke(info)
			}
		}

		fun addCallback(callback: ArticlesCallback) {
			callbacks.add(callback)
		}
	}
}
