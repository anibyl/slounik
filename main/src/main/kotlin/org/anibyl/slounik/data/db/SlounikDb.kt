package org.anibyl.slounik.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.anibyl.slounik.data.db.engbel.EngBelDao
import org.anibyl.slounik.data.db.engbel.EngBelEntity

/**
 * @author Sieva Kimajeŭ
 * @created 2019-08-08
 */
@Deprecated("Replaced by Verbum")
@Database(entities = [EngBelEntity::class], version = 1)
abstract class SlounikDb : RoomDatabase() {
    abstract fun engBelDao(): EngBelDao
}
