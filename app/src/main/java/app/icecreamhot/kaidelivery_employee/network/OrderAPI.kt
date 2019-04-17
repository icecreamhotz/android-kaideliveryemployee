package app.icecreamhot.kaidelivery_employee.network

import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderHistoryResponse
import app.icecreamhot.kaidelivery_employee.model.OrderAndFoodDetail.OrderResponse
import app.icecreamhot.kaidelivery_employee.model.OrderList
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import io.reactivex.Observable
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

    @GET("orders/delivery/employee/worked")
    fun getHistoryOrderEmployee(): Observable<OrderHistoryResponse>

    @GET("orders/delivery/employee/now")
    fun getDeliveryNow(): Observable<OrderList>

    @FormUrlEncoded
    @POST("orders/delivery")
    fun updateEmployeeDelivery(@Field("order_id") order_id: Int): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("orders/update/status")
    fun deleteOrderByID(@Field("order_id") order_id: Int,
                        @Field("order_status") order_status: Int): Observable<ResponseBody>

    companion object {
        fun create(): OrderAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(OrderAPI::class.java)
        }
    }
}