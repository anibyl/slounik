package org.anibyl.slounik.network

import android.content.Context

/**
 * Articles loader interface.
 *
 * @author Usievaład Kimajeŭ
 * @created 23.12.2015
 */
interface ArticlesLoader<in T> where T : ArticlesCallback {
	fun loadArticles(wordToSearch: String, context: Context, callback: T)
}
