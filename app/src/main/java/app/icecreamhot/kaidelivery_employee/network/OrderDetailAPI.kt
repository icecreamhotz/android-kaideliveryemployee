package app.icecreamhot.kaidelivery_employee.network

import app.icecreamhot.kaidelivery_employee.model.OrderTotalPrice
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderDetailAPI {

    @GET("orderdetails/price/{orderId}")
    fun getTotalPrice(@Path("orderId") orderID: Int): Observable<OrderTotalPrice>

    companion object {
        fun create(): OrderDetailAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(OrderDetailAPI::class.java)
        }
    }
}