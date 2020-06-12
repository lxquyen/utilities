package com.steve.utilities.common.di.module

import com.google.gson.Gson
import com.steve.utilities.core.helper.Config
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideGson() = Config.provideGson()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = Config.provideHttpLoggingInterceptor()

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, gson: Gson, logging: HttpLoggingInterceptor) =
        Config.provideRetrofit(url = baseUrl, gson = gson, logging = logging)

}