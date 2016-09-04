package org.anibyl.slounik.network

import android.content.Context

import java.util.ArrayList

/**
 * Batch loader of the dictionary site communicators.
 *
 *
 * Created by Usievaład Čorny on 23.12.15.
 */
class BatchArticlesLoader(vararg communicators:DictionarySiteCommunicator):ArticlesLoader {
	private val communicators:Array<out DictionarySiteCommunicator>

	init {
		this.communicators = communicators
	}

	override fun loadArticles(wordToSearch:String, context:Context, communicatorCallBack:ArticlesCallback) {
		var activeCommunicators = 0

		for (communicator in communicators) {
			if (communicator.enabled()) {
				activeCommunicators++
				val callback = object:ArticlesCallback {
					override fun invoke(info:ArticlesInfo) {
						(communicatorCallBack as BatchArticlesCallback).invoke(this, info)
					}
				}
				(communicatorCallBack as BatchArticlesCallback).addCallback(callback)
				communicator.loadArticles(wordToSearch, context, callback)
			}
		}

		if (activeCommunicators == 0) {
			communicatorCallBack.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
		}
	}

	abstract class BatchArticlesCallback:ArticlesCallback {
		internal var callbacks = ArrayList<ArticlesCallback>()

		internal operator fun invoke(callback:ArticlesCallback, info:ArticlesInfo) {
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

		fun addCallback(callback:ArticlesCallback) {
			callbacks.add(callback)
		}
	}
}