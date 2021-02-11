package org.anibyl.slounik.data

/**
 * Represents article information.

 * Contains articles and connected information.

 * @author Sieva Kimajeŭ
 * @created 2015-04-29
 */
class ArticlesInfo {
	enum class Status {
		SUCCESS,
		IN_PROCESS,
		FAILURE
	}

	var articles: List<Article>? = null
		private set
	var status: Status? = null
		internal set

	constructor(articles: List<Article>?, status: Status) {
		this.articles = articles
		this.status = status
	}

	constructor(articles: List<Article>?) {
		this.articles = articles

		if (articles == null) {
			status = Status.FAILURE
		} else {
			status = Status.SUCCESS
		}
	}

	constructor(status: Status) {
		this.status = status
	}
}
