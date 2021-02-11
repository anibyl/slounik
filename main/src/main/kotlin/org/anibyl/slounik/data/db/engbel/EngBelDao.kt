package org.anibyl.slounik.data.db.engbel

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * @author Sieva Kimajeŭ
 * @created 08.08.2019
 */
@Deprecated("Replaced by Verbum")
@Dao
interface EngBelDao {
	@Query("SELECT * FROM engbel WHERE title LIKE '% ' || :word || ' %' OR title LIKE :word || ' %' OR title LIKE '% ' || :word OR title LIKE :word")
	fun findInTitle(word: String): List<EngBelEntity>

	@Query("SELECT * FROM engbel WHERE title LIKE '% ' || :word || ' %' OR title LIKE :word || ' %' OR title LIKE '% ' || :word OR title LIKE :word OR description LIKE '% ' || :word || ' %' OR description LIKE :word || ' %' OR description LIKE '% ' || :word OR description LIKE :word")
	fun findInTitleOrDescription(word: String): List<EngBelEntity>

	@RawQuery
	fun query(query: SupportSQLiteQuery): Int
}
