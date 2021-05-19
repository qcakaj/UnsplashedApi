package com.ckj.iceandfireapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ckj.iceandfireapplication.db.dao.ImageDao
import com.ckj.iceandfireapplication.db.dao.RemoteKeysDao
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import com.ckj.iceandfireapplication.db.entities.RemoteKeysEntity

@Database(entities = [ImageCacheEntity::class, RemoteKeysEntity::class],version = 1,exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun imageDao() : ImageDao
    abstract fun remoteKeysDao() : RemoteKeysDao

    companion object {
        val DATABASE_NAME = "image_db"
    }
}