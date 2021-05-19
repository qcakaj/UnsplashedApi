package com.ckj.iceandfireapplication.model

import com.ckj.iceandfireapplication.util.randomColor
import com.google.gson.annotations.SerializedName


data class Image(
    var id: String,
    var author: String,
    var imageUrl: String,
    var downloadUrl: String,
    var placeHolderColor: Int= randomColor()
)