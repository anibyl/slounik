package org.anibyl.slounik.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import org.anibyl.slounik.data.db.engbel.EngBelDao
import org.anibyl.slounik.data.db.engbel.EngBelEntity

/**
 * @author Usievaład Kimajeŭ
 * @created 08.08.2019
 */
@Database(entities = [EngBelEntity::class], version = 1)
abstract class SlounikDb : RoomDatabase() {
    abstract fun engBelDao(): EngBelDao
}
