package org.anibyl.slounik.data.db.engbel

import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.RawQuery

/**
 * @author Usievaład Kimajeŭ
 * @created 08.08.2019
 */
@Dao
interface EngBelDao {
	@Query("SELECT * FROM engbel WHERE title LIKE '% ' || :word || ' %' OR title LIKE :word || ' %' OR title LIKE '% ' || :word OR title LIKE :word")
	fun findInTitle(word: String): List<EngBelEntity>

	@Query("SELECT * FROM engbel WHERE title LIKE '% ' || :word || ' %' OR title LIKE :word || ' %' OR title LIKE '% ' || :word OR title LIKE :word OR description LIKE '% ' || :word || ' %' OR description LIKE :word || ' %' OR description LIKE '% ' || :word OR description LIKE :word")
	fun findInTitleOrDescription(word: String): List<EngBelEntity>

	@RawQuery
	fun query(query: SupportSQLiteQuery): Int
}
