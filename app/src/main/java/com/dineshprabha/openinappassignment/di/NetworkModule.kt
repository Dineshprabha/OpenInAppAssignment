package com.dineshprabha.openinappassignment.di

import com.dineshprabha.openinappassignment.api.APIService
import com.dineshprabha.openinappassignment.api.TokenInterceptor
import com.dineshprabha.openinappassignment.utils.Constants
import com.dineshprabha.openinappassignment.utils.Constants.BEARER_TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesAPIService(retrofit: Retrofit) : APIService{
        return retrofit.create(APIService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor : TokenInterceptor): OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(tokenInterceptor).build()
    }

}