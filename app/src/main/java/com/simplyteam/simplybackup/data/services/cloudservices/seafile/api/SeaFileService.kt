package com.simplyteam.simplybackup.data.services.cloudservices.seafile.api

import com.simplyteam.simplybackup.data.models.seafile.File
import com.simplyteam.simplybackup.data.models.seafile.Token
import com.simplyteam.simplybackup.data.models.seafile.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface SeaFileService {

    @POST("api2/auth-token/")
    suspend fun GetAuthToken(@Body user: User) : Token

    @GET("/api2/repos/{repoId}/upload-link/")
    suspend fun GetUploadLink(@Header("Authorization") token: String, @Path("repoId") repoId: String, @Query("p") directory: String) : String

    @Multipart
    @POST
    suspend fun UploadFile(@Url url: String, @Header("Authorization") token: String, @Part("parent_dir") parentDir: RequestBody, @Part file: MultipartBody.Part)

    @GET("/api2/repos/{repoId}/file/")
    suspend fun GetDownloadLink(@Header("Authorization") token: String, @Path("repoId") repoId: String, @Query("p") file: String) : String

    @GET
    suspend fun DownloadFile(@Url url: String, @Header("Authorization") token: String) : ResponseBody

    @GET("/api2/repos/{repoId}/dir/")
    suspend fun GetItemsInDirectory(@Header("Authorization") token: String, @Path("repoId") repoId: String, @Query("p") directory: String) : List<File>

    @DELETE("/api2/repos/{repoId}/file/")
    suspend fun DeleteFile(@Header("Authorization") token: String, @Path("repoId") repoId: String, @Query("p") file: String)

}