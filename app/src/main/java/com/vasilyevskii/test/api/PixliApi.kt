package com.vasilyevskii.test.api

import com.vasilyevskii.test.api.model.DataDTO
import com.vasilyevskii.test.api.model.ImageDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface PixliApi {

    @GET("/get.php")
    fun getDataList(): Call<MutableList<DataDTO>>

    @POST("/send.php")
    fun uploadImage(): Call<MutableList<ImageDTO>>
}