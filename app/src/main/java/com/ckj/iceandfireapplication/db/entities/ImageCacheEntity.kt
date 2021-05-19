package com.ckj.iceandfireapplication.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ckj.iceandfireapplication.util.randomColor

@Entity(tableName = "images")
data class ImageCacheEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "author")
    var author: String,

    @ColumnInfo(name = "width")
    var width: Int?,

    @ColumnInfo(name = "height")
    var height: Int?,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "download_url")
    var downloadUrl: String
) {
    var placeHolderColor= randomColor()
}
