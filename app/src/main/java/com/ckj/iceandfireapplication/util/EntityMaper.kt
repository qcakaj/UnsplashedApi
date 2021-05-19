package com.ckj.iceandfireapplication.util

interface EntityMaper<Entity,DomainModel> {

    fun mapFromEntity(entity:Entity) : DomainModel
    fun mapToEntity(domainModel: DomainModel): Entity

}