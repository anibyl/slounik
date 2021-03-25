package org.anibyl.slounik.activities

import android.content.Context
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.BatchArticlesLoader
import java.util.ArrayList
import javax.inject.Inject

/**
 * Presenter for [SlounikActivity].
 *
 * @author Sieva Kimajeŭ
 * @created 2016-12-26
 */
class SlounikActivityPresenter {
	@Inject lateinit var context: Context
	@Inject lateinit var notifier: Notifier
	@Inject lateinit var loader: BatchArticlesLoader

	internal val articles: ArrayList<Article> = arrayListOf()

	internal var searching = false

	private var activity: SlounikActivity? = null

	init {
		SlounikApplication.graph.inject(this)
	}

	internal fun onActivityCreated(slounikActivity: SlounikActivity) {
		this.activity = slounikActivity
	}

	internal fun onActivityDestroyed() {
		this.activity = null
	}

	internal fun onSearchClicked(wordToSearch: String) {
		articles.clear()
		activity?.articlesUpdated()

		val preparedWord: String = wordToSearch.trim()

		if (preparedWord.isEmpty()) {
			notifier.toast("Nothing to search.", false)
		} else {
			searching = true
			activity?.searchStarted(preparedWord)

			loader.loadArticles(
				preparedWord,
				onProcess = {
					articles.addAll(it)
					activity?.articlesUpdated()
				},
				onFinish = {
					searching = false
					activity?.searchEnded()
				}
			)
		}
	}

	internal fun onStopSearchClicked() {
		loader.cancel()

		searching = false
		activity?.searchEnded()
	}
}
