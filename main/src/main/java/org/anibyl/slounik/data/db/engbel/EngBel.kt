package org.anibyl.slounik.data.db.engbel

import android.arch.persistence.db.SimpleSQLiteQuery
import android.content.Context
import android.content.res.Resources
import org.anibyl.slounik.Notifier
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.data.Article
import org.anibyl.slounik.data.ArticlesCallback
import org.anibyl.slounik.data.ArticlesInfo
import org.anibyl.slounik.data.ArticlesLoader
import org.anibyl.slounik.data.db.SlounikDb
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

/**
 * @author Usievaład Kimajeŭ
 * @created 08.08.2019
 */
class EngBel : ArticlesLoader<ArticlesCallback> {
	@Inject lateinit var preferences: Preferences
	@Inject lateinit var db: SlounikDb
	@Inject lateinit var resources: Resources
	@Inject lateinit var notifier: Notifier

	init {
		SlounikApplication.graph.inject(this)
		initialize()
	}

	override fun loadArticles(wordToSearch: String, context: Context, callback: ArticlesCallback) {
		doAsync {
			val engBelEntities: List<EngBelEntity> = try {
				if (preferences.searchInTitles) {
					db.engBelDao().findInTitle(wordToSearch);
				} else {
					db.engBelDao().findInTitleOrDescription(wordToSearch);
				}
			} catch (t: Throwable) {
				notifier.log("Unable to load eng-bel articles. " + t.message)
				emptyList()
			}

			val dictionaryTitle: String = context.resources.getString(R.string.db_engbel)

			val articles: List<Article> = engBelEntities.map { entity ->
				Article().apply {
					title = entity.title
					description = entity.description
					dictionary = dictionaryTitle
				}
			}

			uiThread {
				callback.invoke(ArticlesInfo(articles))
			}
		}
	}

	override fun enabled(): Boolean {
		return preferences.useEngBel
	}

	private fun initialize() {
		if (!preferences.engBelInitialized) {
			doAsync {
				val sql: String = resources.openRawResource(R.raw.engbel).bufferedReader().use { it.readText() }

				db.engBelDao().query(SimpleSQLiteQuery(sql, null))

				uiThread {
					preferences.engBelInitialized = true
				}
			}
		}
	}
}
