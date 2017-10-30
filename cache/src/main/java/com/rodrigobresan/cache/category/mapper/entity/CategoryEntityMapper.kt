package com.rodrigobresan.cache.category.mapper.entity

import com.rodrigobresan.cache.base.mapper.entity.EntityMapper
import com.rodrigobresan.cache.category.model.CategoryCached
import com.rodrigobresan.cache.movie.model.MovieCached
import com.rodrigobresan.data.model.CategoryEntity
import com.rodrigobresan.data.model.MovieEntity
import javax.inject.Inject

open class CategoryEntityMapper @Inject constructor() : EntityMapper<CategoryCached, CategoryEntity> {

    override fun mapFromCached(cached: CategoryCached): CategoryEntity {
        return CategoryEntity(cached.id, cached.name)
    }

    override fun mapToCached(entity: CategoryEntity): CategoryCached {
        return CategoryCached(entity.id, entity.name)
    }

}