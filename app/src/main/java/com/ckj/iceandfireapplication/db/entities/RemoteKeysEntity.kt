package com.ckj.iceandfireapplication.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey
    val imageId: String,
    val prevKey: Int?,
    val nextKey: Int?
)