package com.rodrigobresan.cache.movie_category

import com.rodrigobresan.cache.category.CategoryQueries.CategoryTable
import com.rodrigobresan.cache.movie.MovieQueries.MovieTable

/**
 * Object containing the queries for the Category table
 */
object MovieCategoryQueries {
    object MovieCategoryTable {

        const val TABLE_NAME = "movie_category"
        const val MOVIE_ID = "movie_id"
        const val CATEGORY_ID = "category_id"

        const val CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        MOVIE_ID + " INTEGER NOT NULL " +
                        "REFERENCES " + MovieTable.TABLE_NAME + "(" + MovieTable.MOVIE_ID + "), " +
                        CATEGORY_ID + " TEXT NOT NULL " +
                        "REFERENCES " + CategoryTable.TABLE_NAME + "(" + CategoryTable.CATEGORY_ID + "), " +
                        "PRIMARY KEY (" + MOVIE_ID + "," + CATEGORY_ID + ")" +
                        ");"

        const val SELECT_ALL = "SELECT * FROM " + TABLE_NAME
    }

    fun getQueryForMovie(movieId: Long, categoryId: String): String {
        return "SELECT * FROM " + MovieCategoryTable.TABLE_NAME +
                " WHERE " + MovieCategoryTable.MOVIE_ID + " = " + movieId +
                " AND " + MovieCategoryTable.CATEGORY_ID + " = '" + categoryId + "'"
    }

}