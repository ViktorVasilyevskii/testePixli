package com.vasilyevskii.test.api

import com.vasilyevskii.test.api.model.DataDTO
import com.vasilyevskii.test.api.model.ImageDTO
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST

interface PixliApi {

    @GET("/get.php")
    fun getDataList(): Single<MutableList<DataDTO>>

    @POST("/send.php")
    fun uploadImage(): Single<MutableList<ImageDTO>>
}