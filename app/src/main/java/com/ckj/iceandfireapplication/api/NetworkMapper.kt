package com.ckj.iceandfireapplication.api

import com.ckj.iceandfireapplication.model.Image
import com.ckj.iceandfireapplication.util.EntityMaper
import javax.inject.Inject

class NetworkMapper @Inject constructor() : EntityMaper<ImageNetworkEntity,Image> {
    override fun mapFromEntity(entity: ImageNetworkEntity): Image {
       return Image(
           id=entity.id,
           author = entity.author,
           imageUrl = entity.url,
           downloadUrl = entity.downloadUrl

       )
    }

    override fun mapToEntity(domainModel: Image): ImageNetworkEntity {
        return ImageNetworkEntity(
            id = domainModel.id,
            author = domainModel.author,
            width = null,
            height = null,
            url = domainModel.imageUrl,
            downloadUrl = domainModel.downloadUrl

        )
    }

    fun mapFromEntityList(entities: List<ImageNetworkEntity>) : List<Image>{
        return entities.map { mapFromEntity(it) }
    }
}