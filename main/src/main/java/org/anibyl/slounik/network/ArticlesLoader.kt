package org.anibyl.slounik.network

import android.content.Context

/**
 * Articles loader interface.
 *
 *
 * Created by Usievaład Čorny on 23.12.15.
 */
interface ArticlesLoader {
	fun loadArticles(wordToSearch:String, context:Context, callBack:ArticlesCallback)
}