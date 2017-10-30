package com.rodrigobresan.cache.test.factory

import com.rodrigobresan.cache.category.model.CategoryCached
import com.rodrigobresan.data.model.CategoryEntity
import com.rodrigobresan.domain.model.MovieCategory

class CategoryFactory {
    companion object Factory {
        fun makeCategoryEntity(): CategoryEntity {
            return CategoryEntity(DataFactory.randomUuid(), DataFactory.randomUuid())
        }

        fun makeCategoryEntityList(count: Int): List<CategoryEntity> {
            val categoryEntityList = mutableListOf<CategoryEntity>()

            repeat(count) {
                categoryEntityList.add(makeCategoryEntity())
            }

            return categoryEntityList
        }

        fun makeCategoryCached(): CategoryCached {
            return CategoryCached(DataFactory.randomUuid(), DataFactory.randomUuid())
        }
    }
}