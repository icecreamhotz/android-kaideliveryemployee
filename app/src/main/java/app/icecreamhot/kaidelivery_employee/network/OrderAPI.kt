package app.icecreamhot.kaidelivery_employee.network

import android.content.Context
import android.content.Intent
import android.util.Log
import app.icecreamhot.kaidelivery_employee.MainActivity
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderHistoryResponse
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderResponse
import app.icecreamhot.kaidelivery_employee.model.OrderList
import app.icecreamhot.kaidelivery_employee.model.ResponseMAS
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import app.icecreamhot.kaidelivery_employee.utils.MY_PREFS
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface OrderAPI {

    @GET("orders/waiting")
    fun getWaitingOrder(): Observable<OrderList>

    @GET("orders/{orderId}")
    fun getOrderAndOrderDetail(@Path(value = "orderId", encoded= true) orderId: Int): Observable<OrderResponse>

    @GET("orders/history/employee")
    fun getHistoryOrderEmployee(@Header("authorization") token: String): Observable<OrderHistoryResponse>

    @GET("orders/delivery/employee/now")
    fun getDeliveryNow(@Header("authorization") token: String): Observable<OrderList>

    @FormUrlEncoded
    @POST("orders/delivery")
    fun updateEmployeeDelivery(@Field("order_id") order_id: Int, @Header("authorization") token: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("orders/update/status")
    fun updateStatusOrder(@Field("order_id") order_id: Int,
                          @Field("order_status") order_status: Int,
                          @Field("order_statusdetails") order_statusdetails: String?,
                          @Field("message") message: String?,
                          @Field("token") getHistoryOrderEmployeetoken: String?,
                          @Header("authorization") jwtToken: String): Observable<ResponseMAS>

    @FormUrlEncoded
    @POST("orders/update/timeout")
    fun updateTimeOut(@Field("order_id") order_id: Int,
                      @Field("timeout") order_timeout: String,
                      @Field("foodprice") food_price: Double,
                      @Header("authorization") jwtToken: String): Observable<ResponseMAS>

    @FormUrlEncoded
    @POST("orders/queue/previous")
    fun updatePreviousQueue(@Field("order_id") order_id: Int): Observable<ResponseMAS>

    @FormUrlEncoded
    @POST("orders/queue/next")
    fun updateNextQueue(@Field("order_id") order_id: Int,
                            @Field("order_queue") order_queue: Int): Observable<ResponseMAS>

    @FormUrlEncoded
    @POST("orders/update/customprice")
    fun updateCustomPrice(@Field("order_id") order_id: Int,
                        @Field("order_price") order_price: Double): Observable<ResponseMAS>

    companion object {
        fun create(context: Context): OrderAPI {
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

            return retrofit.create(OrderAPI::class.java)
        }
    }
}