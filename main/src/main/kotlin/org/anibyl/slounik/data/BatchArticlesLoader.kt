package org.anibyl.slounik.data

import org.anibyl.slounik.data.ArticlesInfo.Status.FINISHED
import org.anibyl.slounik.data.ArticlesInfo.Status.IN_PROCESS

/**
 * Batch loader for article loaders.
 *
 * @author Sieva Kimajeŭ
 * @created 2015-12-23
 */
class BatchArticlesLoader(private vararg val loaders: ArticlesLoader<ArticlesCallback>) {
	private var onProcess: OnProcess? = null
	private var onFinish: OnFinish? = null
	private var loaderCallbacks: HashSet<ArticlesCallback> = HashSet()

	fun loadArticles(wordToSearch: String, onProcess: OnProcess, onFinish: OnFinish) {
		if (this.onFinish != null) {
			cancel()
		}

		this.onProcess = onProcess
		this.onFinish = onFinish

		for (loader in loaders) {
			if (!loader.enabled()) {
				continue
			}

			val articlesCallback = object : ArticlesCallback {
				override fun invoke(info: ArticlesInfo) {
					if (!loaderCallbacks.contains(this)) {
						return
					}

					when (info.status) {
						FINISHED -> {
							loaderCallbacks.remove(this)

							onProcess(info)

							if (loaderCallbacks.size == 0) {
								finish()
							}
						}

						IN_PROCESS -> onProcess(info)
					}
				}
			}

			loaderCallbacks.add(articlesCallback)

			loader.loadArticles(wordToSearch, articlesCallback)
		}

		if (loaderCallbacks.isEmpty()) {
			finish()
		}
	}

	fun cancel() {
		loaderCallbacks.clear()

		for (loader in loaders) {
			loader.cancel()
		}

		finish()
	}

	private fun onProcess(info: ArticlesInfo) {
		onProcess?.accept(info.articles)
	}

	private fun finish() {
		onFinish?.accept()

		onProcess = null
		onFinish = null
	}

	fun interface OnProcess {
		fun accept(articles: List<Article>)
	}

	fun interface OnFinish {
		fun accept()
	}
}
