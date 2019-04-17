package app.icecreamhot.kaidelivery_employee.network

import app.icecreamhot.kaidelivery_employee.model.ResponseMAS
import app.icecreamhot.kaidelivery_employee.utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface EmployeeAPI {

    @FormUrlEncoded
    @POST("orders/delivery")
    fun updateEmployeeStatus(@Field("work_status") status: Int): Observable<ResponseMAS>


    companion object {
        fun create(): EmployeeAPI {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(EmployeeAPI::class.java)
        }
    }
}