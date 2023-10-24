package com.example.clienteswithapi.di

import com.example.clienteswithapi.data.remote.ClientesApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Singleton
    @Provides
    fun providesMiClientesApi(moshi: Moshi): ClientesApi {
        return Retrofit.Builder().baseUrl("http://prestamospersonales.somee.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
            .create(ClientesApi::class.java)
    }
}