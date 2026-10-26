package com.example.data.api

import com.example.data.model.LocationItem
import com.example.data.model.VmItem
import com.example.data.model.VmModifyRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IdCloudHostApi {

    @GET("v1/locations")
    suspend fun getLocations(
        @Header("apikey") apiKey: String
    ): Response<List<LocationItem>>

    @GET("v1/{slug}/user-resource/vm/list")
    suspend fun getVmListBySlug(
        @Header("apikey") apiKey: String,
        @Path("slug") slug: String
    ): Response<List<VmItem>>

    @GET("v1/user-resource/vm/list")
    suspend fun getVmListGlobal(
        @Header("apikey") apiKey: String
    ): Response<List<VmItem>>

    // Start VM with JSON Body or Form urlencoded
    @POST("v1/{slug}/user-resource/vm/start")
    @FormUrlEncoded
    suspend fun startVmBySlug(
        @Header("apikey") apiKey: String,
        @Path("slug") slug: String,
        @Field("uuid") uuid: String
    ): Response<ResponseBody>

    @POST("v1/user-resource/vm/start")
    @FormUrlEncoded
    suspend fun startVmGlobal(
        @Header("apikey") apiKey: String,
        @Field("uuid") uuid: String
    ): Response<ResponseBody>

    // Stop VM
    @POST("v1/{slug}/user-resource/vm/stop")
    @FormUrlEncoded
    suspend fun stopVmBySlug(
        @Header("apikey") apiKey: String,
        @Path("slug") slug: String,
        @Field("uuid") uuid: String,
        @Field("force") force: Boolean = false
    ): Response<ResponseBody>

    @POST("v1/user-resource/vm/stop")
    @FormUrlEncoded
    suspend fun stopVmGlobal(
        @Header("apikey") apiKey: String,
        @Field("uuid") uuid: String,
        @Field("force") force: Boolean = false
    ): Response<ResponseBody>

    // Modify VM
    @PATCH("v1/{slug}/user-resource/vm")
    suspend fun modifyVmBySlug(
        @Header("apikey") apiKey: String,
        @Path("slug") slug: String,
        @Body request: VmModifyRequest
    ): Response<ResponseBody>

    @PATCH("v1/user-resource/vm")
    suspend fun modifyVmGlobal(
        @Header("apikey") apiKey: String,
        @Body request: VmModifyRequest
    ): Response<ResponseBody>
}
