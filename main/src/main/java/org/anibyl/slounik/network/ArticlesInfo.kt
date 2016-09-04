package org.anibyl.slounik.network

import java.util.ArrayList

/**
 * Represents article information.

 * Contains articles and connected information.

 * Created by Usievaład Čorny on 29.4.15 16.08.
 */
class ArticlesInfo {
	enum class Status {
		SUCCESS,
		IN_PROCESS,
		FAILURE
	}

	var articles:ArrayList<Article>? = null
		private set
	var status:Status? = null
		internal set

	constructor(articles:ArrayList<Article>?, status:Status) {
		this.articles = articles
		this.status = status
	}

	constructor(articles:ArrayList<Article>?) {
		this.articles = articles

		if (articles == null) {
			status = Status.FAILURE
		} else {
			status = Status.SUCCESS
		}
	}

	constructor(status:Status) {
		this.status = status
	}
}