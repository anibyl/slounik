package org.anibyl.slounik.data.db.engbel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Sieva Kimajeŭ
 * @created 2019-08-08
 */
@Deprecated("Replaced by Verbum")
@Entity(tableName = "engbel")
data class EngBelEntity(
		@ColumnInfo(name = "title") val title: String, @ColumnInfo(name = "description") val description: String
) {
	@ColumnInfo(name = "id")
	@PrimaryKey(autoGenerate = true) var id: Long = 0
}
