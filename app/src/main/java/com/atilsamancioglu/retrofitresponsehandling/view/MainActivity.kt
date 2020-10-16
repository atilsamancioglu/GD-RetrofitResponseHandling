package com.atilsamancioglu.retrofitresponsehandling.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atilsamancioglu.retrofitresponsehandling.R
import com.atilsamancioglu.retrofitresponsehandling.adapter.RecyclerViewAdapter
import com.atilsamancioglu.retrofitresponsehandling.model.CryptoModel
import com.atilsamancioglu.retrofitresponsehandling.service.CryptoAPI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.Listener {
    private val BASE_URL = "https://raw.githubusercontent.com/"
    private var cryptoModels: ArrayList<CryptoModel>? = null
    private var recyclerViewAdapter : RecyclerViewAdapter? = null

    //Disposable
    private var compositeDisposable: CompositeDisposable? = null

    private var job : Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //https://raw.githubusercontent.com/atilsamancioglu/K21-JSONDataSet/master/crypto.json
        //https://api.nomics.com/v1/prices?key=2187154b76945f2373394aa34f7dc98a
        //2187154b76945f2373394aa34f7dc98a

        compositeDisposable = CompositeDisposable()

        //RecyclerView

        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        loadData()

    }

    private fun loadData() {

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build().create(CryptoAPI::class.java)


        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = retrofit.getData()

            withContext(Dispatchers.Main){
                if(response.isSuccessful) {
                    response.body()?.let {
                        cryptoModels = ArrayList(it)
                        cryptoModels?.let {
                            recyclerViewAdapter = RecyclerViewAdapter(it,this@MainActivity)
                            recyclerView.adapter = recyclerViewAdapter
                        }
                    }
                }
            }
        }
        /*
        compositeDisposable?.add(retrofit.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))


         */

        /*

        val service = retrofit.create(CryptoAPI::class.java)

        val call = service.getData()



        call.enqueue(object: Callback<List<CryptoModel>> {
            override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<List<CryptoModel>>,
                response: Response<List<CryptoModel>>
            ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            cryptoModels = ArrayList(it)

                            cryptoModels?.let {
                                recyclerViewAdapter = RecyclerViewAdapter(it,this@MainActivity)
                                recyclerView.adapter = recyclerViewAdapter
                            }


                           for (cryptoModel : CryptoModel in cryptoModels!!) {
                               println(cryptoModel.currency)
                               println(cryptoModel.price)

                           }



                        }
                    }

            }

        })


         */

    }

    private fun handleResponse(cryptoList : List<CryptoModel>){
        cryptoModels = ArrayList(cryptoList)

        cryptoModels?.let {
            recyclerViewAdapter = RecyclerViewAdapter(it,this@MainActivity)
            recyclerView.adapter = recyclerViewAdapter
        }
    }

    override fun onItemClick(cryptoModel: CryptoModel) {
        Toast.makeText(this,"Clicked : ${cryptoModel.currency}", Toast.LENGTH_LONG ).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable?.clear()
        job?.cancel()
    }

}