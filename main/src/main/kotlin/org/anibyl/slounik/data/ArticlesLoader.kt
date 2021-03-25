package org.anibyl.slounik.data

/**
 * Articles loader interface.
 *
 * @author Sieva Kimajeŭ
 * @created 2015-12-23
 */
interface ArticlesLoader<in T> where T : ArticlesCallback {
	fun loadArticles(wordToSearch: String, callback: T)

	fun cancel()

	fun enabled(): Boolean
}
