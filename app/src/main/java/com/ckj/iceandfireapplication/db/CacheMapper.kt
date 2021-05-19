package com.ckj.iceandfireapplication.db

import com.ckj.iceandfireapplication.db.entities.ImageCacheEntity
import com.ckj.iceandfireapplication.model.Image
import com.ckj.iceandfireapplication.util.EntityMaper
import javax.inject.Inject

class CacheMapper @Inject constructor() : EntityMaper<ImageCacheEntity,Image> {
    override fun mapFromEntity(entity: ImageCacheEntity): Image {
        return Image(
            id=entity.id,
            author = entity.author,
            imageUrl = entity.url,
            downloadUrl = entity.downloadUrl
        )
    }

    override fun mapToEntity(domainModel: Image): ImageCacheEntity {
       return ImageCacheEntity(
           id= domainModel.id,
           author = domainModel.author,
           width = null,
           height = null,
           url=domainModel.imageUrl,
           downloadUrl = domainModel.downloadUrl
       )
    }

    fun mapFromEntityList(entities:List<ImageCacheEntity>) : List<Image> {
        return entities.map { mapFromEntity(it) }
    }

    fun mapToEntityList(entities: List<Image>) : List<ImageCacheEntity> {
        return entities.map { mapToEntity(it) }
    }
}