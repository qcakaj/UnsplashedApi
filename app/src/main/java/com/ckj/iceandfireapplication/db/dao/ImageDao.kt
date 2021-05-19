package com.ckj.iceandfireapplication.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(images: List<ImageCacheEntity>)

    @Query("SELECT * FROM images")
     fun getImages(): PagingSource<Int, ImageCacheEntity>

    @Query("DELETE FROM images")
    suspend fun clearImages()

}