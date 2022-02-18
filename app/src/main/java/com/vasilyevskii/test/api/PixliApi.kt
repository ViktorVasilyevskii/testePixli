package com.vasilyevskii.test.api

import com.vasilyevskii.test.api.model.DataDTO
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface PixliApi {

    @GET("/get.php")
    fun getDataList(@Query("get_bodyParts") get_bodyParts: Int): Single<MutableList<DataDTO>>

    @POST("/send.php")
    fun uploadImage(@Query("send_data") send_data: Int,
                    @Part id: Int,
                    @Part image: MultipartBody.Part,
                    @Part array: Array<String>): Single<ResponseBody>
}