package com.atilsamancioglu.retrofitresponsehandling.service

import com.atilsamancioglu.retrofitresponsehandling.model.CryptoModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface CryptoAPI {

    //GET, POST, UPDATE, DELETE

    //https://raw.githubusercontent.com/
    // atilsamancioglu/K21-JSONDataSet/master/crypto.json

    //https://api.nomics.com/v1/
    // prices?key=2187154b76945f2373394aa34f7dc98a

    @GET("atilsamancioglu/K21-JSONDataSet/master/crypto.json")
    suspend fun getData(): Response<List<CryptoModel>>

    //fun getData(): Observable<List<CryptoModel>>
    //fun getData(): Call<List<CryptoModel>>


}