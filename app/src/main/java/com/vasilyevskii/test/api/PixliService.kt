package com.vasilyevskii.test.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PixliService {

    private val baseUrl = "https://test-job.pixli.app"

    private var pixliApi: PixliApi

    init{
        val retrofit = createRetrofit()
        pixliApi = retrofit.create(PixliApi::class.java)
    }

    fun getPixliApi(): PixliApi {
        return pixliApi
    }

    private fun createOkHttpClient(): OkHttpClient{
        return OkHttpClient.Builder().build()
    }

    private fun createRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}