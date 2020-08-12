package id.cikup.covid19cart

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET("summary")
    fun getAllNegara(): Call<AllNegara>
}

interface InfoService {
    @GET
    fun getInfoService(@Url url: String?): Call<List<InfoNegara>>
}
