package org.anibyl.slounik.activities

import android.content.Context
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.network.Article
import org.anibyl.slounik.network.ArticlesInfo
import org.anibyl.slounik.network.BatchArticlesLoader
import org.anibyl.slounik.network.RodnyjaVobrazy
import org.anibyl.slounik.network.Server
import org.anibyl.slounik.network.Skarnik
import org.anibyl.slounik.network.SlounikOrg
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
	@Inject lateinit var config: Server.Config
	@Inject lateinit var notifier: Notifier
	@Inject lateinit var slounikOrg: SlounikOrg
	@Inject lateinit var skarnik: Skarnik
	@Inject lateinit var rodnyjaVobrazy: RodnyjaVobrazy

	internal val articles: ArrayList<Article> = arrayListOf()
	internal val title: String
		get() = lastSearchedWord ?: context.getString(R.string.app_name)

	internal var lastSearchedWord: String? = null
	internal var searching = false

	private val loader: BatchArticlesLoader

	private var activity: SlounikActivity? = null

	init {
		SlounikApplication.graph.inject(this)

		loader = BatchArticlesLoader(slounikOrg, skarnik, rodnyjaVobrazy)
	}

	internal fun onActivityCreated(slounikActivity: SlounikActivity) {
		this.activity = slounikActivity
	}

	internal fun onActivityDestroyed() {
		this.activity = null
	}

	internal fun onSearchClicked(wordToSearch: String) {
		articles.clear()
		activity?.resetArticles()

		if (wordToSearch == "") {
			// TODO Make it visible for everyone.
			notifier.toast("Nothing to onSearchClicked.", true)
		} else {
			lastSearchedWord = wordToSearch
			searching = true
			activity?.onStartSearching(wordToSearch)

			loader.loadArticles(wordToSearch, context, object : BatchArticlesLoader.BatchArticlesCallback() {
				override fun invoke(info: ArticlesInfo) {
					val loadedArticles = info.articles
					if (loadedArticles != null) {
						articles.addAll(loadedArticles)
					}

					// TODO Move down.
					when (info.status) {
						ArticlesInfo.Status.SUCCESS, ArticlesInfo.Status.FAILURE -> {
							searching = false
							activity?.resetControls()
						}
					}

					activity?.articlesUpdated()
				}
			})
		}
	}
}
