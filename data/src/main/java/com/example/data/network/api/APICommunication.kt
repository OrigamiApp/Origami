package com.example.data.network.api

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.data.network.models.ApiResponse
import com.example.data.network.models.request.TagRequestApi
import com.example.data.network.models.response.Tag
import com.example.data.network.models.response.UserMe
import com.example.data.network.util.AddCookiesInterceptor
import com.example.data.network.util.ReceivedCookiesInterceptor
import com.example.data.network.util.RequestExecutor
import com.example.data.portability.Consumer
import com.example.data.portability.Supplier
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.reactivestreams.Subscriber
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class APICommunication(private val retrofitAPI: RetrofitAPI, private val requestExecutor: RequestExecutor) : APIService {

    companion object {
        fun <T> execute(observable: Observable<T>) {
            val subscription = observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({result -> Log.e("Succes", "" + result) },
                            {error -> Log.e("Error", error.message)})
        }

        fun getInstance(context: Context, coockieCOnsumer: Consumer<String>?, cookieSupplier: Supplier<String>?): APICommunication {
            val okHttpClient = OkHttpClient().newBuilder().addInterceptor(AddCookiesInterceptor(cookieSupplier))
                                                          .addInterceptor(ReceivedCookiesInterceptor(coockieCOnsumer))
                                                          .build();

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://theorigamiapp.herokuapp.com/")
                .client(okHttpClient)
                .build();

            val retrofitAPI = retrofit.create(RetrofitAPI::class.java)

            val requestExecutor = RequestExecutor(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager);
            return APICommunication(retrofitAPI, requestExecutor);
        }

    }

    override fun getTags(): Observable<ApiResponse<List<Tag>>> {
        return requestExecutor.execute(retrofitAPI.getTags())
    }

    override fun postTags(body: TagRequestApi): Observable<Void> {
        return requestExecutor.execute(retrofitAPI.postTags(body))
    }

    override fun deleteTag(id: Int): Observable<Unit> {
        return requestExecutor.execute(retrofitAPI.deleteTag(id))
    }

    override fun login(accessToken: String): Observable<ApiResponse<UserMe>> {
        return requestExecutor.execute(retrofitAPI.login(accessToken))
    }
}
