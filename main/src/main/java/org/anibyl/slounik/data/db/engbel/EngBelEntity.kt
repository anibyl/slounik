package org.anibyl.slounik.data.db.engbel

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * @author Usievaład Kimajeŭ
 * @created 08.08.2019
 */
@Entity(tableName = "engbel")
data class EngBelEntity(
		@ColumnInfo(name = "title") val title: String, @ColumnInfo(name = "description") val description: String
) {
	@ColumnInfo(name = "id")
	@PrimaryKey(autoGenerate = true) var id: Long = 0
}
