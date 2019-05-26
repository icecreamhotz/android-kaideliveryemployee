package app.icecreamhot.kaidelivery_employee.network

import android.content.Context
import android.content.Intent
import android.util.Log
import app.icecreamhot.kaidelivery_employee.MainActivity
import app.icecreamhot.kaidelivery_employee.model.OrderTotalPrice
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import app.icecreamhot.kaidelivery_employee.utils.MY_PREFS
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderDetailAPI {

    @GET("orderdetails/price/{orderId}")
    fun getTotalPrice(@Path("orderId") orderID: Int): Observable<OrderTotalPrice>

    companion object {
        fun create(context: Context): OrderDetailAPI {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.addInterceptor(object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    val response = chain.proceed(request)
                    if(response.code() == 401) {
                        val shared = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
                        shared.edit().remove("token").apply()
                        Log.d("logout", shared.getString("token", "not"))
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    return response
                }
            })
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(OrderDetailAPI::class.java)
        }
    }
}