package com.rodrigobresan.cache.movie.impl

import android.database.sqlite.SQLiteDatabase
import com.rodrigobresan.cache.PreferencesHelper
import com.rodrigobresan.cache.db.DbOpenHelper
import com.rodrigobresan.cache.movie.mapper.db.MovieDbMapper
import com.rodrigobresan.cache.mapper.MovieEntityMapper
import com.rodrigobresan.cache.movie.MovieQueries
import com.rodrigobresan.cache.movie.MovieQueries.MovieTable
import com.rodrigobresan.data.model.CategoryEntity
import com.rodrigobresan.data.model.MovieCategoryEntity
import com.rodrigobresan.data.model.MovieEntity
import com.rodrigobresan.data.repository.movie.movie.MovieCache
import com.rodrigobresan.data.repository.movie.movie.movie_category.CategoryCache
import com.rodrigobresan.data.repository.movie.movie.movie_category.MovieCategoryCache
import com.rodrigobresan.domain.model.MovieCategory
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class MovieCacheImpl @Inject constructor(dbOpenHelper: DbOpenHelper,
                                         private val categoryCache: CategoryCache,
                                         private val movieCategoryCache: MovieCategoryCache,
                                         private val movieEntityMapper: MovieEntityMapper,
                                         private val movieDbMapper: MovieDbMapper,
                                         private val preferences: PreferencesHelper) : MovieCache {

    private val CACHE_EXPIRATION_TIME = (0.5 * 10 * 1000)

    private var database: SQLiteDatabase = dbOpenHelper.writableDatabase

    fun getDatabase(): SQLiteDatabase {
        return database
    }

    override fun clearMovies(): Completable {
        return Completable.defer {
            database.beginTransaction()
            try {
                database.delete(MovieTable.TABLE_NAME, null, null)
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }

            Completable.complete()
        }
    }

    override fun saveMovies(movieCategory: MovieCategory, movies: List<MovieEntity>): Completable {
        return Completable.defer {
            database.beginTransaction()

            try {
                movies.forEach {
                    insertMovie(it)
                    insertCategory(movieCategory.name)
                    insertMovieCategory(movieCategory, it)
                }
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }

            Completable.complete()
        }
    }

    private fun insertMovieCategory(movieCategory: MovieCategory, movie: MovieEntity) {
        movieCategoryCache.saveMovieCategory(MovieCategoryEntity(movie.id, movieCategory.name))
    }

    private fun insertCategory(categoryName: String) {
        // TODO need to figure out about the name of the category.. but not now :D
        categoryCache.saveCategories(mutableListOf(CategoryEntity(categoryName, categoryName)))
    }

    private fun insertMovie(it: MovieEntity) {
        database.insertWithOnConflict(MovieTable.TABLE_NAME, null,
                movieDbMapper.toContentValues(movieEntityMapper.mapToCached(it)), SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun getMovies(movieCategory: MovieCategory): Single<List<MovieEntity>> {
        return Single.defer<List<MovieEntity>> {

            val query = MovieQueries.getQueryForMoviesOnCategory(movieCategory.name)

            val updatesCursor = database.rawQuery(query, null)
            val movies = mutableListOf<MovieEntity>()

            while (updatesCursor.moveToNext()) {
                val cachedMovie = movieDbMapper.fromCursor(updatesCursor)
                movies.add(movieEntityMapper.mapFromCached(cachedMovie))
            }

            updatesCursor.close()
            Single.just<List<MovieEntity>>(movies)
        }
    }

    override fun isCached(): Boolean {
        return database.rawQuery(MovieTable.SELECT_ALL, null).count > 0
    }

    override fun setLastCacheTime(lastCacheTime: Long) {
        preferences.lastCacheTime = lastCacheTime
    }

    override fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdate = this.preferences.lastCacheTime

        return currentTime - lastUpdate > CACHE_EXPIRATION_TIME
    }

}