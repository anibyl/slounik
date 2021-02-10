package org.anibyl.slounik.data

import android.content.Context
import java.util.ArrayList

/**
 * Batch loader for article loaders.
 *
 * @author Usievaład Kimajeŭ
 * @created 23.12.2015
 */
class BatchArticlesLoader(private vararg val loaders: ArticlesLoader<ArticlesCallback>) {
	fun loadArticles(wordToSearch: String, context: Context, callback: BatchArticlesCallback) {
		var activeLoaders = 0

		for (loader in loaders) {
			if (loader.enabled()) {
				activeLoaders++
				val articlesCallback = object : ArticlesCallback {
					override fun invoke(info: ArticlesInfo) {
						callback.invoke(this, info)
					}
				}
				callback.addCallback(articlesCallback)
				loader.loadArticles(wordToSearch, context, articlesCallback)
			}
		}

		if (activeLoaders == 0) {
			callback.invoke(ArticlesInfo(ArticlesInfo.Status.FAILURE))
		}
	}

	abstract class BatchArticlesCallback : ArticlesCallback {
		private var callbacks = ArrayList<ArticlesCallback>()

		internal operator fun invoke(callback: ArticlesCallback, info: ArticlesInfo) {
			if (!callbacks.contains(callback)) {
				// Should not happen, however, I had couple occurrences in production.
				return
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
