package org.anibyl.slounik.data

import android.content.Context

/**
 * Articles loader interface.
 *
 * @author Sieva Kimajeŭ
 * @created 23.12.2015
 */
interface ArticlesLoader<in T> where T : ArticlesCallback {
	fun loadArticles(wordToSearch: String, context: Context, callback: T)

	fun enabled(): Boolean
}
