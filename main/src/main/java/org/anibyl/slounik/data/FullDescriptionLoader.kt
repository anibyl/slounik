package org.anibyl.slounik.data

/**
 * @author Usievaład Kimajeŭ
 * @created 08.08.2019
 */
interface FullDescriptionLoader {
	fun loadArticleDescription(article: Article, callback: ArticlesCallback)
}
