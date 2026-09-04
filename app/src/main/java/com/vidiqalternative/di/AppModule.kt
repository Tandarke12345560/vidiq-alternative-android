package com.vidiqalternative.di

import android.content.Context
import com.vidiqalternative.data.api.OpenRouterApiService
import com.vidiqalternative.data.api.YouTubeApiService
import com.vidiqalternative.data.web.DDGSearchService
import com.vidiqalternative.data.web.WebFetchService
import com.vidiqalternative.data.repository.ModelRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideYouTubeApiService(): YouTubeApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenRouterApiService(gson: Gson): OpenRouterApiService {
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenRouterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDDGSearchService(okHttpClient: OkHttpClient): DDGSearchService {
        return DDGSearchService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideWebFetchService(okHttpClient: OkHttpClient): WebFetchService {
        return WebFetchService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideModelRepository(
        @ApplicationContext context: Context,
        openRouterService: OpenRouterApiService
    ): ModelRepository {
        return ModelRepository(context, openRouterService)
    }
}
