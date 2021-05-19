package com.ckj.iceandfireapplication.api

import com.google.gson.annotations.SerializedName

data class ImageNetworkEntity(
    @SerializedName("id")
    var id: String,
    @SerializedName("author")
    var author: String,
    @SerializedName("width")
    var width: Int?,
    @SerializedName("height")
    var height: Int?,
    @SerializedName("url")
    var url: String,
    @SerializedName("download_url")
    var downloadUrl: String
)