package com.rodrigobresan.cache.test.factory

import com.rodrigobresan.cache.movie_category.model.MovieCategoryCached
import com.rodrigobresan.data.model.MovieCategoryEntity

class MovieCategoryFactory {
    companion object Factory {
        fun makeMovieCategoryEntity() : MovieCategoryEntity {
            return MovieCategoryEntity(DataFactory.randomLong(), DataFactory.randomUuid())
        }

        fun makeMovieCategoryCached(): MovieCategoryCached {
            return MovieCategoryCached(DataFactory.randomLong(), DataFactory.randomUuid())
        }
    }
}