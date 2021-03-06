package com.rodrigobresan.presentation.movie_details.presenter

import com.rodrigobresan.domain.movie_detail.interactor.GetMovieDetails
import com.rodrigobresan.domain.movie_detail.model.MovieDetail
import com.rodrigobresan.domain.movies.interactor.FavoriteMovie
import com.rodrigobresan.domain.movies.interactor.UnfavoriteMovie
import com.rodrigobresan.presentation.movie_details.contract.MovieDetailsContract
import com.rodrigobresan.presentation.movie_details.mapper.MovieDetailsMapper
import io.reactivex.observers.DisposableSingleObserver
import javax.inject.Inject

/**
 * Presenter class for MovieDetails section
 */
class MovieDetailsPresenter @Inject constructor(val connectionStatus: com.rodrigobresan.data.connection.ConnectionStatus,
                                                val movieDetailsView: MovieDetailsContract.View,
                                                val getMovieDetails: GetMovieDetails,
                                                val favoriteMoviesUseCase: FavoriteMovie,
                                                val unfavoriteMovie: UnfavoriteMovie,
                                                val movieDetailsMapper: MovieDetailsMapper) : MovieDetailsContract.Presenter {

    init {
        movieDetailsView.setPresenter(this)
    }

    override fun start() {
    }

    override fun stop() {
        getMovieDetails.dispose()
    }

    override fun favoriteMovie(movieId: Long) {
        favoriteMoviesUseCase.execute(movieId)
                .subscribe({
                    System.out.println("DONE")
                })
    }

    override fun unfavoriteMovie(movieId: Long) {
        unfavoriteMovie.execute(movieId)
                .subscribe({
                    System.out.println("DONE")
                })
    }

    override fun loadMovieDetails(movieId: Long) {
        movieDetailsView.showProgress()
        getMovieDetails.execute(MovieDetailsSubscriber(), movieId)
    }

    private fun hideAllViews() {
        movieDetailsView.hideProgress()
        movieDetailsView.hideErrorState()
        movieDetailsView.hideEmptyState()
    }

    inner class MovieDetailsSubscriber : DisposableSingleObserver<MovieDetail>() {
        override fun onError(e: Throwable) {
            hideAllViews()
            movieDetailsView.showErrorState()
            checkConnectionStatus(false)
        }

        override fun onSuccess(movieDetail: MovieDetail) {
            hideAllViews()

            if (movieDetail != null) {
                movieDetailsView.showMovieDetails(movieDetailsMapper.mapToView(movieDetail))
                checkConnectionStatus(true)
            } else {
                movieDetailsView.showEmptyState()
                checkConnectionStatus(false)
            }
        }
    }

    private fun checkConnectionStatus(hasData: Boolean) {
        if (connectionStatus.isOffline()) {
            if (hasData) {
                movieDetailsView.showOfflineModeCachedData()
            } else {
                movieDetailsView.showOfflineModeNoCachedData()
            }
        }
    }

}
