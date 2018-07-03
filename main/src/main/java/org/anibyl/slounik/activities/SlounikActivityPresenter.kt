package org.anibyl.slounik.activities

import android.content.Context
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.network.Article
import org.anibyl.slounik.network.ArticlesInfo
import org.anibyl.slounik.network.BatchArticlesLoader
import java.util.ArrayList
import javax.inject.Inject

/**
 * Presenter for [SlounikActivity].
 *
 * @author Usievaład Kimajeŭ
 * @created 26.12.2016
 */
class SlounikActivityPresenter {
	@Inject lateinit var context: Context
	@Inject lateinit var notifier: Notifier
	@Inject lateinit var loader: BatchArticlesLoader

	internal val articles: ArrayList<Article> = arrayListOf()
	internal val title: String
		get() = lastSearchedWord ?: context.getString(R.string.app_name)

	internal var lastSearchedWord: String? = null
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

		if (wordToSearch == "") {
			notifier.toast("Nothing to search.", false)
		} else {
			lastSearchedWord = wordToSearch
			searching = true
			activity?.lockControls()

			loader.loadArticles(wordToSearch, context, object : BatchArticlesLoader.BatchArticlesCallback() {
				override fun invoke(info: ArticlesInfo) {
					val loadedArticles = info.articles
					if (loadedArticles != null) {
						articles.addAll(loadedArticles)
					}

					activity?.articlesUpdated()

					when (info.status) {
						ArticlesInfo.Status.SUCCESS, ArticlesInfo.Status.FAILURE -> {
							searching = false
							activity?.unlockControls()
						}
					}
				}
			})
		}
	}
}
