package org.anibyl.slounik.data

import android.content.Context

/**
 * Articles loader interface.
 *
 * @author Sieva Kimajeŭ
 * @created 2015-12-23
 */
interface ArticlesLoader<in T> where T : ArticlesCallback {
	fun loadArticles(wordToSearch: String, context: Context, callback: T)

	fun enabled(): Boolean
}
