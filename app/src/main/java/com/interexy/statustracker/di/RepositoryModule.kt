package com.interexy.statustracker.di


import com.interexy.statustracker.data.repository.StatusTrackerRepositoryImpl
import com.interexy.statustracker.doman.repository.StatusTrackerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun cornPosRepository(
            beStarRepositoryImpl: StatusTrackerRepositoryImpl
        ): StatusTrackerRepository


}